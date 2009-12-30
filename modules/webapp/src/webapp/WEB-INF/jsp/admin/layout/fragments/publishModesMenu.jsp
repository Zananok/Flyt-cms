<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License
--%>

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<div class="buttonGroup search">
    <form action="" method="get">
        <input type="text" id="SearchQuery" class="query content" name="query content">
        <input type="submit" id="SearchButton" value="" title="<kantega:label key="aksess.search.submit"/>">
    </form>
</div>
<div class="buttonGroup">
    <a href="${pageContext.request.contextPath}/admin/publish/Navigate.action" class="button"><span class="view"><kantega:label key="aksess.mode.view"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="#" class="button"><span class="edit"><kantega:label key="aksess.mode.edit"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="${pageContext.request.contextPath}/admin/publish/Organize.action" class="button last"><span class="organize"><kantega:label key="aksess.mode.organize"/></span></a>
</div>
<div class="buttonGroup">
    <a href="#" class="button"><span class="linkcheck"><kantega:label key="aksess.mode.linkcheck"/></span></a>
    <span class="buttonSeparator"></span>
    <a href="#" class="button last"><span class="statistics"><kantega:label key="aksess.mode.statistics"/></span></a>
</div>

