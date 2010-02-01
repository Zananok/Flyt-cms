/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.modules.mailsubscription.data.MailSubscription;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.service.SecurityService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class for sending mailsubscriptions notifying users about new content (for newsletters etc)
 */
public class MailSubscriptionAgent {
    private static final String SOURCE = "aksess.MailSubscriptionAgent";

    /**
     *
     * @param content - List of new content
     * @param subscriptions - list of subscriptions
     * @param site - Which site to send newsletter for
     * @throws ConfigurationException -
     * @throws SystemException -
     */
    public void sendEmail(List<Content> content, List<MailSubscription> subscriptions, Site site) throws  ConfigurationException, SystemException {
        Configuration config = Aksess.getConfiguration();

        String baseurl = Aksess.getBaseUrl();

        String alias = ".";
        if (site != null && !site.getAlias().equals("/")) {
            alias = site.getAlias();
            alias = alias.replace('/', '.');
            baseurl = site.getDefaultBaseUrl();
        }


        // Parameters may be given site specific using "mail.alias..." or global for all sites "mail..."
        String from = config.getString("mail" + alias + "from");
        if (from == null) {
            from = config.getString("mail.from");
            if (from == null) {
                throw new ConfigurationException("mail.from", SOURCE);
            }
        }

        String subject = config.getString("mail" + alias + "subscription.subject", null);
        if (subject == null) {
            subject = config.getString("mail.subscription.subject", "Nyhetsbrev");
        }

        String template = config.getString("mail" + alias + "subscription.template", null);
        if (template == null) {
            template = config.getString("mail.subscription.template", "maillist.vm");
        }


        Map<String, List<Content>> subscribers = new HashMap<String, List<Content>>();

        for (MailSubscription subscription : subscriptions) {
            String email = subscription.getEmail();
            if (email.indexOf("@") != -1) {
                List<Content> subscriberContent = subscribers.get(email);
                if (subscriberContent == null) {
                    subscriberContent = new ArrayList<Content>();
                    subscribers.put(email, subscriberContent);
                }
                
                for (int j = 0; j < content.size(); j++) {
                    // Check if user subscribes to content
                    Content c = (Content)content.get(j);

                    if(isSubscriptionMatch(subscription, c, site)) {
                        subscriberContent.add(c);
                    }

                }
            }
        }

        for (String email : subscribers.keySet()) {
            // Send email to this user
            Map<String, Object> param = new HashMap<String, Object>();

            List<Content> subscriberContent = subscribers.get(email);
            if (subscriberContent != null && subscriberContent.size() > 0) {
                param.put("contentlist", subscriberContent);
                param.put("baseurl", baseurl);

                try {
                    MailSender.send(from, email, subject, template, param);
                } catch (Exception e) {
                    Log.error(SOURCE, e, null, null);
                }
            }

        }
    }

    private boolean isSubscriptionMatch(MailSubscription subscription, Content c, Site site) {
        List<Association> associations = c.getAssociations();
        for (Association a : associations) {
            if (site == null || site.getId() == a.getSiteId()) {
                // Correct site
                if ((subscription.getChannel() > 0 && subscription.getChannel() == a.getParentAssociationId()) ||
                        (subscription.getDocumenttype() > 0 && subscription.getDocumenttype() == c.getDocumentTypeId()) ) {
                    return true;
                }
            }
        }
        return false;
    }


    public void emailNewContentSincePreviousDate(Date previousRun, String interval, Site site) throws SystemException, ConfigurationException, SQLException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            if (previousRun != null) {
                ContentQuery query = new ContentQuery();
                query.setPublishDateFrom(previousRun);
                if (site != null) {
                    query.setSiteId(site.getId());
                }

                List allContentList = ContentAO.getContentList(query, -1, null, false);

                // This job only sends notificiation about content which is viewable by everyone, all protected content is excluded
                List<Content> contentList = new ArrayList<Content>();
                Role everyone = new Role();
                everyone.setId(Aksess.getEveryoneRole());

                for (int i = 0; i < allContentList.size(); i++) {
                    Content content = (Content) allContentList.get(i);
                    if (SecurityService.isAuthorized(everyone, content, Privilege.VIEW_CONTENT)) {
                        contentList.add(content);
                        Log.debug(SOURCE, "New content:" + content.getTitle(), null, null);
                    }
                }


                // Cut descriptions and remove tags
                for (Content content : contentList) {
                    String body = content.getDescription();
                    body = body.replaceAll("<(.|\\n)+?>", "");
                    if (body.length() > 400) {
                        body = body.substring(0, 399) + "...";
                    }
                    content.setDescription(body);
                }

                if (contentList.size() > 0) {
                    List<MailSubscription> subscriptions = new ArrayList<MailSubscription>();
                    PreparedStatement st = c.prepareStatement("select * from mailsubscription where MailInterval = ? order by Email");
                    st.setString(1, interval);
                    ResultSet rs = st.executeQuery();
                    while(rs.next()) {
                        MailSubscription s = new MailSubscription();
                        s.setChannel(rs.getInt("Channel"));
                        s.setDocumenttype(rs.getInt("Documenttype"));
                        s.setLanguage(rs.getInt("Language"));
                        s.setEmail(rs.getString("Email"));

                        subscriptions.add(s);
                    }

                    sendEmail(contentList, subscriptions, site);
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL-error", SOURCE, e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
