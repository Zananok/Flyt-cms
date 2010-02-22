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

package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.enums.AssociationType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;

public class AssociationAOHelper {
    private static final String SOURCE = "aksess.AssociationAOHelper";

    public static void fixDefaultPostings() throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            List sites = SiteCache.getSites();

            // MySQL st�tter ikke � oppdatere tabeller som er med i subqueries, derfor denne tungvinte m�ten � gj�re det p�
            String query = "SELECT min(uniqueid) from associations WHERE siteid = ? AND type = " + AssociationType.CROSS_POSTING + ") AND (IsDeleted IS NULL OR IsDeleted = 0) AND contentid NOT IN " +
                            " (SELECT contentid from associations WHERE siteid = ? AND type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " AND (IsDeleted IS NULL OR IsDeleted = 0)) GROUP BY contentid";
            PreparedStatement st = c.prepareStatement(query);

            String updateQuery = "UPDATE associations SET type = " + AssociationType.DEFAULT_POSTING_FOR_SITE + " WHERE uniqueid = ? AND (IsDeleted IS NULL OR IsDeleted = 0)";
            PreparedStatement updateSt = c.prepareStatement(updateQuery);

            for (int i = 0; i < sites.size(); i++) {
                Site site =  (Site)sites.get(i);
                st.setInt(1, site.getId());
                st.setInt(2, site.getId());
                st.setInt(3, site.getId());
                ResultSet rs = st.executeQuery();
                while(rs.next()) {
                    int id = rs.getInt(1);
                    updateSt.setInt(1, id);
                    updateSt.executeUpdate();
                }
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }


    public static void deleteShortcuts() throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            if (dbConnectionFactory.isMySQL()) {
                // MySQL st�tter ikke � slette tabeller som er med i subqueries, derfor denne tungvinte m�ten � gj�re det p�
                String query = "SELECT UniqueId FROM associations WHERE type = " + AssociationType.SHORTCUT + " AND AssociationId NOT IN (SELECT UniqueId FROM associations WHERE (IsDeleted IS NULL OR IsDeleted = 0)) AND (IsDeleted IS NULL OR IsDeleted = 0)";
                PreparedStatement st = c.prepareStatement(query);

                String updateQuery = "DELETE FROM associations WHERE UniqueId = ?";
                PreparedStatement updateSt = c.prepareStatement(updateQuery);

                ResultSet rs = st.executeQuery();
                while(rs.next()) {
                    int id = rs.getInt(0);
                    updateSt.setInt(1, id);
                    updateSt.executeUpdate();
                }
                rs.close();
                rs = null;


            } else {
                PreparedStatement st = c.prepareStatement("DELETE FROM associations WHERE type = " + AssociationType.SHORTCUT + " AND AssociationId NOT IN (SELECT UniqueId FROM associations WHERE (IsDeleted IS NULL OR IsDeleted = 0)) AND (IsDeleted IS NULL OR IsDeleted = 0)");
                st.executeUpdate();
                st.close();
                st = null;
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Feil ved databasekall", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }
}
