<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.configuration.Configuration,
                 no.kantega.commons.util.LocaleLabels,
                 no.kantega.publishing.admin.content.util.HTMLEditorHelper,
                 no.kantega.publishing.common.Aksess,
                 no.kantega.publishing.common.cache.SiteCache"%>
<%@ page import="no.kantega.publishing.common.data.Content"%>
<%@ page import="no.kantega.publishing.common.data.Site"%>
<%@ page import="no.kantega.publishing.common.data.attributes.HtmltextAttribute"%>
<%@ page import="no.kantega.publishing.common.service.ContentManagementService"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="java.io.*"%>
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
<%
    HtmltextAttribute attribute = (HtmltextAttribute)request.getAttribute("attribute");
    Content   content   = (Content)request.getAttribute("content");
    String    fieldName = (String)request.getAttribute("fieldName");
    int height = attribute.getHeight();
    int width = attribute.getWidth();
    String value = attribute.getValue();
    HTMLEditorHelper helper = new HTMLEditorHelper(request);
    value = helper.preEditFilter(value);

    Configuration conf = Aksess.getConfiguration();

    String confPrefix = "editor." + attribute.getFeatureSet() + ".";

    Site site = SiteCache.getSiteById(content.getAssociation().getSiteId());

    boolean hasHtmlEditorRole = false;
    String[] htmlEditorRole = Aksess.getHtmlEditorRoles();
    if (htmlEditorRole != null) {
        ContentManagementService cms = new ContentManagementService(request);
        SecuritySession securitySession = cms.getSecuritySession();
        hasHtmlEditorRole = securitySession.isUserInRole(htmlEditorRole);
    }

    // Let etter /css/site/editor.css og /site/css/editor.css
    String cssPath = "/css" + site.getAlias() + attribute.getCss();
    InputStream is = pageContext.getServletContext().getResourceAsStream(cssPath);
    if (is == null) {
        cssPath = site.getAlias() + "css/" + attribute.getCss();
    }
    is.close();
%>
<div class="heading"><%=attribute.getTitle()%><%if (attribute.isMandatory()) {%> <span class="mandatory">*</span><%}%></div>
<div class="inputs">
    <TEXTAREA name="<%=fieldName%>" id="<%=fieldName%>" cols="30"><%=value%></TEXTAREA><BR>

    <script type="text/javascript">
        tinyMCE.init({
            // General options
            language : 'en', // en / nb
            mode : "exact",
            elements : "<%=fieldName%>",
            theme : "advanced",

            skin : "o2k7",
            skin_variant : "silver",

            plugins : "aksess_insertlink,aksess_insertmedia,aksess_inserttable,safari,table,searchreplace,contextmenu,paste,fullscreen,noneditable",

            // TODO: gjennomgang av gyldige elementer og attributter
            valid_elements : "@[id|class|style|title|dir<ltr?rtl|lang|xml::lang|onclick|ondblclick|"
                    + "onmousedown|onmouseup|onmouseover|onmousemove|onmouseout|onkeypress|"
                    + "onkeydown|onkeyup],a[rel|rev|charset|hreflang|tabindex|accesskey|type|"
                    + "name|href|target|title|class|onfocus|onblur],strong/b,em/i,strike,u,"
                    + "#p,-ol[type|compact],-ul[type|compact],-li,br,img[longdesc|usemap|"
                    + "src|border|alt=|title|hspace|vspace|width|height|align],-sub,-sup,"
                    + "-blockquote,-table[border=0|cellspacing|cellpadding|width|frame|rules|"
                    + "height|align|summary|bgcolor|background|bordercolor],-tr[rowspan|width|"
                    + "height|align|valign|bgcolor|background|bordercolor],tbody,thead,tfoot,"
                    + "#td[colspan|rowspan|width|height|align|valign|bgcolor|background|bordercolor"
                    + "|scope],#th[colspan|rowspan|width|height|align|valign|scope],caption,-div,"
                    + "-span,-code,-pre,address,-h1,-h2,-h3,-h4,-h5,-h6,hr[size|noshade],-font[face"
                    + "|size|color],dd,dl,dt,cite,abbr,acronym,del[datetime|cite],ins[datetime|cite],"
                    + "object[classid|width|height|codebase|*],param[name|value|_value],embed[type|width"
                    + "|height|src|*],script[src|type],map[name],area[shape|coords|href|alt|target],bdo,"
                    + "button,col[align|char|charoff|span|valign|width],colgroup[align|char|charoff|span|"
                    + "valign|width],dfn,fieldset,form[action|accept|accept-charset|enctype|method],"
                    + "input[accept|alt|checked|disabled|maxlength|name|readonly|size|src|type|value],"
                    + "kbd,label[for],legend,noscript,optgroup[label|disabled],option[disabled|label|selected|value],"
                    + "q[cite],samp,select[disabled|multiple|name|size],small,"
                    + "textarea[cols|rows|disabled|name|readonly],tt,var,big",

            // skulle det ikke v�re slik?
            width : "${attribute.width}",   // 600 
            height : "${attribute.height}", // 400

            // Theme options
            // underline
            theme_advanced_buttons1 : "styleselect,formatselect,|,bold,italic,|,justifyleft,justifycenter,justifyright,|,bullist,numlist,|,outdent,indent",
            theme_advanced_buttons2 : "undo,redo,|,cut,copy,paste,pastetext,removeformat,cleanup,|,search,replace,|,link,unlink,anchor,|,image,testplugin",
            theme_advanced_buttons3 : "aTable,|,row_props,cell_props,|,row_before,row_after,delete_row,|,col_before,col_after,delete_col,|,split_cells,merge_cells,|,sub,sup,charmap,|,fullscreen,|,code",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_statusbar_location : "bottom",
            theme_advanced_resizing : false,

            // Example content CSS (should be your site CSS)
            //content_css : "${cssPath}",
            content_css : '../css/editor.css',
        });
    </script>
</div>
