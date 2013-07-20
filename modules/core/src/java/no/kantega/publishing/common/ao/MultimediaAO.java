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
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.spring.RootContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MultimediaAO {

    private static PermissionAO permissionAO;
    public static MultimediaDao multimediaDao;

    /**
     * Henter multimedia objekt fra basen (unntatt data)
     * @param id - Id til objekt som skal hentes
     * @return
     * @throws SystemException
     */
    @Deprecated
    public static Multimedia getMultimedia(int id) throws SystemException {
        setPermissionAOIfNotSet();
        return multimediaDao.getMultimedia(id);
    }

    /**
     * Henter multimedia objekt fra basen (unntatt data)
     * @param parentId - parentId til objekt som skal hentes
     * @param name - navn på objekt til som skal hentes
     * @return
     * @throws SystemException
     */
    @Deprecated
    public static Multimedia getMultimediaByParentIdAndName(int parentId, String name) throws SystemException {
        setPermissionAOIfNotSet();
        return multimediaDao.getMultimediaByParentIdAndName(parentId, name);
    }

    /**
     * Sender multimedia til klienten
     * @param id - Id på objekt som skal streames
     * @param ish - Inputhandler som håndterer stream
     * @throws SystemException
     */
    public static void streamMultimediaData(int id, InputStreamHandler ish) throws SystemException {
        setPermissionAOIfNotSet();
        multimediaDao.streamMultimediaData(id, ish);
    }


    /**
     * Henter alle objekter i multimediaarkiv med angitt parentId
     * @param parentId - id til foreldremappe
     * @return
     * @throws SystemException
     */
    public static List<Multimedia> getMultimediaList(int parentId) throws SystemException {
        setPermissionAOIfNotSet();
        return multimediaDao.getMultimediaList(parentId);
    }

    /**
     * Henter antall objekter i multimediaarkiv.
     *
     * @return antall objekter i multimediaarkiv.
     * @throws SystemException
     */
    public static int getMultimediaCount() throws SystemException {
        setPermissionAOIfNotSet();
        return multimediaDao.getMultimediaCount();
    }

    /**
     * Flytter et multimediaobjekt
     * @param mmId - Id til objekt som skal flyttes
     * @param newParentId - Ny plassering for objekt
     * @throws SystemException
     */
    public static void moveMultimedia(int mmId, int newParentId) throws SystemException {
        multimediaDao.moveMultimedia(mmId, newParentId);

    }

    /**
     * Lagre multimedia objekt i basen
     * @param mm Multimediaobjekt
     * @return
     * @throws SystemException
     */
    public static int setMultimedia(Multimedia mm) throws SystemException {

        multimediaDao.setMultimedia(mm);

        if (mm.getParentId() == 0 && mm.getSecurityId() == -1) {
            setPermissionAOIfNotSet();
            permissionAO.setPermissions(mm, null);
            mm.setSecurityId(mm.getId());
        }

        return mm.getId();
    }


    private static void setSecurityIdForChildren(Connection c, int parentId, int oldSecurityId, int newSecurityId) throws SQLException {
        ResultSet rs = SQLHelper.getResultSet(c, "select Id, Type from multimedia where ParentId = ? and SecurityId = ?" , new Object[]{parentId , oldSecurityId});
        PreparedStatement st = c.prepareStatement("update multimedia set SecurityId = ? where Id = ?");

        while(rs.next()) {
            int id = rs.getInt("Id");
            MultimediaType type = MultimediaType.getMultimediaTypeAsEnum(rs.getInt("Type"));
            st.setInt(1, newSecurityId);
            st.setInt(2, id);
            st.execute();
            if (type == MultimediaType.FOLDER) {
                setSecurityIdForChildren(c, id, oldSecurityId, newSecurityId);
            }
        }
    }


    /**
     * TODO: These methods should be moved to a new non static MultimediaPermissionsDao class
     * Setter securityId til angitt objekt, samt alle underobjekter lik angitt objekts id
     * @param c - Databasekopling
     * @param object - objekt som det skal settes ny securityid for
     * @throws SQLException -
     */
    public static void setSecurityId(Connection c, BaseObject object, boolean setFromParent) throws SQLException {
        int securityId = object.getId();
        if (setFromParent) {
            Multimedia mm = (Multimedia)object;
            securityId = mm.getParentId();
        }
        PreparedStatement st = c.prepareStatement("update multimedia set SecurityId = ? where Id = ?");
        st.setInt(1, securityId);
        st.setInt(2, object.getId());
        st.execute();
        setSecurityIdForChildren(c, object.getId(), object.getSecurityId(), securityId);
    }

    private static void setPermissionAOIfNotSet(){
        if(permissionAO == null){
            permissionAO = RootContext.getInstance().getBean(PermissionAO.class);
            multimediaDao = RootContext.getInstance().getBean(MultimediaDao.class);
        }
    }
}
