<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ /*
  ~  * Copyright 2009 Kantega AS
  ~  *
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<div id="AssociatedRoles">

    <table>
        <c:forEach var="role" items="${roles}" varStatus="status">
            <tr  class="tableRow${status.index mod 2}">
                <td>${role.name}</td>
                <c:if test="${canDelete}">
                    <td align="right"><a href="ListAssociatedRoles.action?topicMapId=${topic.topicMapId}&amp;topicId=${topic.id}&amp;deleteId=${role.id}" target="_new" class="button delete"><span><kantega:label key="aksess.button.delete"/></span></a></td>
                </c:if>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${canAdd}">
        <div class="buttonGroup">
            <select name="AddRole" id="AddRole">
                <c:forEach var="role" items="${allRoles}">
                    <option value="${role.id}">${role.name}</option>
                </c:forEach>
            </select>
            <span class="button"><input type="button" class="add" value="<kantega:label key="aksess.button.add"/>"></span>
        </div>
    </c:if>

    <div class="ui-state-highlight">
        <kantega:label key="aksess.viewtopic.associatedroles.help"/>
    </div>
</div>
