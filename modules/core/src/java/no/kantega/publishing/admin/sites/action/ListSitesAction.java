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

package no.kantega.publishing.admin.sites.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import no.kantega.publishing.api.cache.SiteCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 4, 2009
 * Time: 11:03:49 AM
 */
public class ListSitesAction extends AbstractController {
    private SiteCache siteCache;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("sites", siteCache.getSites());

        List<Integer> existingSiteIds = new ArrayList<Integer>();
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            ResultSet rs = SQLHelper.getResultSet(c, "SELECT SiteId FROM associations");
            while (rs.next()) {
                existingSiteIds.add(rs.getInt(1));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        model.put("existingSiteIds", existingSiteIds);

        return new ModelAndView("/WEB-INF/jsp/admin/sites/listsites.jsp", model);
    }

    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
