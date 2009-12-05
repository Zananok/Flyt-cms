<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.client.util.RequestParameters"%>
<%@ page import="java.util.Locale" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.security.data.User" %>
<%@ page import="no.kantega.publishing.security.data.Role" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="title">
    <kantega:label key="aksess.editpermissions.title"/> - ${title}
</kantega:section>

<kantega:section id="head">
    <script language="Javascript" type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/autocomplete.js"></script>
</kantega:section>

<kantega:section id="body">
    <script language="Javascript">
        function addRole() {
            var rolewin = window.open("SelectRoles.action", "rolewin", "toolbar=no,width=450,height=350,resizable=yes,scrollbars=no");
            rolewin.focus();
        }
        function addUser() {
            var rolewin = window.open("SelectUsers.action", "rolewin", "toolbar=no,width=450,height=400,resizable=yes,scrollbars=no");
            rolewin.focus();
        }

        function changePermission(id) {
            var notificationDiv = document.getElementById("notificationDiv_" + id);
            if (notificationDiv) {
                var priv = parseInt(document.getElementById("role_" + id).value, 10);
                if (priv < ${minNotificationPrivilege}) {
                    notificationDiv.style.display = 'none';
                } else {
                    notificationDiv.style.display = 'block';
                }
            }
        }
    </script>

    <div id="EditPermissionsForm">
        <form action="" name="linkform">
            <div class="fieldset">
                <fieldset>
                    <h1><kantega:label key="aksess.editpermissions.title"/> - ${title}</h1>

                    <div class="padded">
                        <table border="0" width="580" cellspacing="0" cellpadding="0">
                            <!-- Rettigheter -->
                            <tr>
                                <th width="200"><kantega:label key="aksess.editpermissions.grouporuser"/></th>
                                <th width="100" align="center" class="tableHeading"><kantega:label key="aksess.editpermissions.privilege"/></th>
                                <th width="80">&nbsp;</th>
                                <th width="220" align="center" class="tableHeading"><kantega:label key="aksess.editpermissions.notification"/></th>
                            </tr>
                            <tr>
                                <td colspan="4">
                                    <form name="permissionsForm" action="SavePermissions.action" method="post">
                                        <div style="height: 250px; overflow-y:auto">
                                            <table border="0" cellspacing="0" cellpadding="0" width="580">
                                                <!-- Permissions -->
                                                <c:forEach var="p" items="${permissionsList}" varStatus="status">
                                                    <tr class="tableRow${status.index mod 2}">
                                                        <input type="hidden" name="roletype_${p.securityIdentifier.id}" value="${p.securityIdentifier.type}">
                                                        <td width="200">${p.securityIdentifier.name}</td>
                                                        <td width="100">
                                                            <c:choose>
                                                                <c:when test="${canModifyPermissions}">
                                                                    <select name="role_${p.securityIdentifier.id}" id="role_${status.index}" onchange="changePermission('${status.index}')">
                                                                        <c:forEach var="privilege" items="${privileges}">
                                                                            <option value="${privilege}" <c:if test="${privilege == p.privilege}">selected="selected"</c:if>><kantega:label key="aksess.editpermissions.priv${privilege}"/></option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <kantega:label key="aksess.editpermissions.priv${p.privilege}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td width="80">
                                                            <c:choose>
                                                                <c:when test="${canModifyPermissions}">
                                                                    <a href="RemovePermission.action?removeId=${status.index}" class="button delete"><span><kantega:label key="aksess.button.delete"/></span></a>
                                                                </c:when>
                                                                <c:otherwise>&nbsp;</c:otherwise>
                                                            </c:choose>

                                                        </td>
                                                        <td width="200">
                                                            <c:if test="${! empty priorities}">
                                                                <div id="notificationDiv_${status.index}" <c:if test="${p.privilege < minNotificationPrivilege}">style="display:none;"</c:if>>
                                                                    <c:choose>
                                                                        <c:when test="${canModifyPermissions}">
                                                                            <select name="notification_${p.securityIdentifier.id}">
                                                                                <c:forEach var="priority" items="${priorities}">
                                                                                    <option value="${priority}" <c:if test="${priority == p.notificationPriority}">selected="selected"</c:if>><kantega:label key="aksess.editpermissions.notification${priority}"/></option>
                                                                                </c:forEach>
                                                                            </select>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:if test="${p.notificationPriority != null}">
                                                                                <kantega:label key="aksess.editpermissions.notification${p.notificationPriority}"/>
                                                                            </c:if>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </table>
                                        </div>
                                    </form>

                                </td>
                            </tr>
                            <c:if test="${canModifyPermissions}">
                                <tr>
                                    <td colspan="4" align="right">
                                        <a href="Javascript:addRole('<%=new Role().getType()%>')" class="button add"><kantega:label key="aksess.editpermissions.addrole"/></a><br>
                                        <a href="Javascript:addUser('<%=new User().getType()%>')" class="button add"><kantega:label key="aksess.editpermissions.adduser"/></a>
                                    </td>
                                </tr>
                            </c:if>
                        </table>
                        <c:if test="${canModifyPermissions}">
                            <c:if test="${objSecurityId == objectId}">
                                <div class="info">
                                            <kantega:label key="aksess.editpermissions.editpermissionsfor"/> <strong>${title}</strong><br>
                                            <kantega:label key="aksess.editpermissions.willbeupdated"/>
                                </div>
                            </c:if>
                            <c:if test="${objSecurityId != objectId && inheritedTitle != ''}">
                                <div class="info">
                                            <strong>${title}</strong> <kantega:label key="aksess.editpermissions.inheritfrom"/> <a href="EditPermissins.action?id=${objectId}&type=${permissionsObject.objectType}">${inheritedTitle}</a>.<br><br>
                                            <kantega:label key="aksess.editpermissions.inheritfrom2"/>
                                </div>
                            </c:if>
                        </c:if>
                        <c:if test="${!canModifyPermissions}">
                            <div class="info">
                                <kantega:label key="aksess.editpermissions.readonly"/>
                            </div>
                        </c:if>
                    </div>
                    <div class="buttonGroup">
                        <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                        <span class="button"><input type="button" onclick="window.close()" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
                    </div>
                </fieldset>
            </div>
        </form>

    </div>
</kantega:section>
<%@ include file="../layout/popupLayout.jsp" %>



