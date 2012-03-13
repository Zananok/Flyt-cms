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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.data.ContentIdentifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 *
 */
public class GetUrlTag extends TagSupport {
    private static String SOURCE = "aksess.GetUrlTag";

    String url = null;
    String queryParams = null;
    boolean addcontextpath = true;
    boolean escapeurl = true;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setQueryparams(String queryParams) {
        this.queryParams = queryParams;
    }

    public void setAddcontextpath(boolean addcontextpath){
       this.addcontextpath = addcontextpath;
    }

    public void setEscapeurl(boolean escapeurl) {
        this.escapeurl = escapeurl;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            url = AttributeTagHelper.replaceMacros(url, pageContext);

            String absoluteurl = addcontextpath ? Aksess.getContextPath(): "";

            if (url != null && url.length() > 0) {
                if (url.indexOf("http:") != -1 || url.indexOf("https:") != -1 ) {
                    absoluteurl = url;
                } else {
                    if (url.charAt(0) == '/') {
                        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

                        // Hvis adminmodus, legg til siteid p� link
                        if (HttpHelper.isAdminMode(request)) {
                            try {
                                ContentIdentifier cid = new ContentIdentifier(request, url);
                                if (url.indexOf("?") == -1) {
                                    url = url + "?siteId=" + cid.getSiteId();
                                } else {
                                    url = url + "&amp;siteId=" + cid.getSiteId();
                                }

                            } catch (ContentNotFoundException e) {

                            }
                        }

                        absoluteurl += url;
                    } else {
                        absoluteurl += "/" + url;
                    }
                }


            }

            if (queryParams != null) {
                if ((!queryParams.startsWith("&")) && (!queryParams.startsWith("?")) && (!queryParams.startsWith("#"))) {
                    if (absoluteurl.indexOf("?") == -1) {
                        queryParams = "?" + queryParams;
                    } else {
                        queryParams = "&" + queryParams;
                    }
                }
                queryParams = queryParams.replaceAll("&", "&amp;");
                absoluteurl = absoluteurl + queryParams;
            }

            if ( !escapeurl ){
                absoluteurl = absoluteurl.replaceAll("&amp;", "&");
            }

            out.write(absoluteurl);

        } catch (IOException e) {
            throw new JspException("ERROR: GetUrlTag", e);
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
            throw new JspException("ERROR: GetUrlTag", e);
        } catch (NotAuthorizedException e) {
            // Do nothing
        }

        url = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

}
