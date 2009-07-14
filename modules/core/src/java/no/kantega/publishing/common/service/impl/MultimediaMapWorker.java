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

package no.kantega.publishing.common.service.impl;

import no.kantega.publishing.common.data.MultimediaMapEntry;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.commons.exception.SystemException;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultimediaMapWorker {
    private static final String SOURCE = "aksess.MultimediaMapWorker";

    private static MultimediaMapEntry getFirst(int parentId, List entries) {
        for (int i = 0; i < entries.size(); i++) {
            MultimediaMapEntry e = (MultimediaMapEntry)entries.get(i);
            // Snarveier kan aldri v�re parents
            if (e.parentId == parentId) {
                entries.remove(e);
                return e;
            }
        }
        return null;
    }

    private static void addToSiteMap(MultimediaMapEntry parent, List entries) {
        int parentId = parent.currentId;
        MultimediaMapEntry entry = getFirst(parentId, entries);
        while (entry != null) {
            addToSiteMap(entry, entries);
            parent.addChild(entry);
            entry = getFirst(parentId, entries);
        }
    }

    private static MultimediaMapEntry getSiteMapBySQL(String query) throws SystemException {

        List tmpentries = new ArrayList();

        MultimediaMapEntry sitemap = new MultimediaMapEntry(0, 0, MultimediaType.FOLDER, "Multimediaarkiv");

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, query);
            while(rs.next()) {
                int id = rs.getInt(1);
                int parentId = rs.getInt(2);
                int type    = rs.getInt(3);
                String title = rs.getString(4);

                MultimediaMapEntry entry = new MultimediaMapEntry(id, parentId, MultimediaType.getMultimediaTypeAsEnum(type), title);
                entry.setSecurityId(rs.getInt(5));
                tmpentries.add(entry);
            }
            rs.close();
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

        if (sitemap != null) {
            // Vi har funnet starten p� sitemap'en, legg til underelementer
            addToSiteMap(sitemap, tmpentries);
        }
        return sitemap;
    }


    public static MultimediaMapEntry getSiteMap() throws SystemException {
        StringBuffer query = new StringBuffer();

        query.append("select Id, ParentId, Type, Name, SecurityId from multimedia order by ParentId, Type, Name");
        return getSiteMapBySQL(query.toString());
    }


    public static MultimediaMapEntry getPartialSiteMap(int[] idList, boolean getOnlyFolders) throws SystemException {
        StringBuffer query = new StringBuffer();
        query.append("select Id, ParentId, Type, Name, SecurityId from multimedia where ParentId in (0");
        if (idList != null) {
            for (int i = 0; i < idList.length; i++) {
                query.append("," + idList[i]);
            }
        }
        query.append(") ");
        if (getOnlyFolders) {
            query.append(" and Type = " + MultimediaType.FOLDER.getTypeAsInt());
        }
        query.append(" order by ParentId, Type, Name");
        return getSiteMapBySQL(query.toString());
    }

    /**
     *  Metoder for testing, skriver ut sitemap over hele nettstedet...
     */

    private static void printSiteMap(MultimediaMapEntry sitemap, int depth) {
        if (sitemap != null) {
            for (int i = 0; i < depth; i++) {
                System.out.print("---");
            }
            System.out.print(sitemap.title + "(" + sitemap.currentId + ")");
            System.out.print("\n");
            List children = sitemap.getChildren();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    printSiteMap((MultimediaMapEntry)children.get(i), depth + 1);
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        MultimediaMapEntry entry = getSiteMap();

        printSiteMap(entry, 0);
    }
}
