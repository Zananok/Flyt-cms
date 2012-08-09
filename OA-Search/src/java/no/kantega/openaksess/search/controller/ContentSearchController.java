package no.kantega.openaksess.search.controller;

import com.google.gdata.util.common.base.Pair;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Performs search for Aksess content.
 */
public class ContentSearchController implements AksessController {
    @Autowired
    private Searcher searcher;

    @Autowired
    private SiteCache siteCache;

    private boolean searchAllSites = false;
    private boolean showOnlyVisibleContent = true;
    private boolean showOnlyPublishedContent = true;
    private List<String> facetFields;
    private List<String> facetQueries;

    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String query = getQuery(request);
        if (isNotEmpty(query)) {
            SearchResponse searchResponse = performSearch(request, query);
            model.put("searchResponse", searchResponse);

            String urlPrefix = "?";

            model.put("facetUrls", getFacetUrls(urlPrefix, searchResponse));

            int currentPage = searchResponse.getCurrentPage();
            if (currentPage > 0) {
                String prevPageUrl = QueryStringGenerator.getPrevPageUrl(searchResponse.getQuery(), currentPage);
                model.put("prevPageUrl", urlPrefix + prevPageUrl);
            }

            int numberOfPages = searchResponse.getNumberOfPages();
            if (currentPage < numberOfPages) {
                String nextPageUrl = QueryStringGenerator.getNextPageUrl(searchResponse.getQuery(), currentPage);
                model.put("nextPageUrl", urlPrefix + nextPageUrl);
            }

            if (numberOfPages > 1) {
                model.put("pageUrls", QueryStringGenerator.getPageUrls(searchResponse, currentPage, urlPrefix));
            }
        }

        return model;
    }

    private Map<String, String> getFacetUrls(String urlPrefix, SearchResponse searchResponse) {
        Map<String,String> facetUrls = new HashMap<String, String>();
        for (Map.Entry<String, Collection<Pair<String, Number>>> facetFieldEntry : searchResponse.getFacets().entrySet()) {
            for(Pair<String, Number> facetFieldValue : facetFieldEntry.getValue()){
                String facetName = facetFieldEntry.getKey();
                facetUrls.put(String.format("%s.%s", facetName, facetFieldValue.first), urlPrefix + QueryStringGenerator.getFacetUrl(facetName + ":" + facetFieldValue.first, searchResponse.getQuery()));
            }
        }

        return facetUrls;
    }

    private SearchResponse performSearch(HttpServletRequest request, String query) {
        AksessSearchContext searchContext = getSearchContext(request);
        return searcher.search(getSearchQuery(request, query, searchContext));
    }

    private SearchQuery getSearchQuery(HttpServletRequest request, String query, AksessSearchContext searchContext) {
        SearchQuery searchQuery = new SearchQuery(searchContext, query, getFilterQueries(request, searchContext));

        searchQuery.setFacetFields(facetFields);

        searchQuery.setFacetQueries(facetQueries);
        return searchQuery;
    }

    private List<String> getFilterQueries(HttpServletRequest request, AksessSearchContext searchContext) {
        List<String> filterQueries = Arrays.asList(ServletRequestUtils.getStringParameters(request, QueryStringGenerator.FILTER_PARAM));

        if(!searchAllSites){
            filterQueries.add("siteId:" + searchContext.getSiteId());
        }
        if(showOnlyVisibleContent){
            filterQueries.add("visibilityStatus:" + ContentVisibilityStatus.getName(ContentVisibilityStatus.ACTIVE));
        }
        if(showOnlyPublishedContent){
            filterQueries.add("contentStatus:" + ContentStatus.getContentStatusAsString(ContentStatus.PUBLISHED));
        }

        return filterQueries;
    }

    private String getQuery(HttpServletRequest request) {
        return ServletRequestUtils.getStringParameter(request, QueryStringGenerator.QUERY_PARAM, "");
    }

    private AksessSearchContext getSearchContext(HttpServletRequest request) {
        return new AksessSearchContext(SecuritySession.getInstance(request), findSiteId(request));
    }

    private int findSiteId(HttpServletRequest request) {
        int siteId = 1;

        Content content = (Content)request.getAttribute("aksess_this");
        if (content != null) {
            siteId = content.getAssociation().getSiteId();
        } else {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }
        }
        return siteId;
    }

    public String getDescription() {
        return "Performs search for Aksess content";
    }

    public void setSearchAllSites(boolean searchAllSites) {
        this.searchAllSites = searchAllSites;
    }

    public void setShowOnlyVisibleContent(boolean showOnlyVisibleContent) {
        this.showOnlyVisibleContent = showOnlyVisibleContent;
    }

    public void setShowOnlyPublishedContent(boolean showOnlyPublishedContent) {
        this.showOnlyPublishedContent = showOnlyPublishedContent;
    }

    public void setFacetFields(List<String> facetFields) {
        this.facetFields = facetFields;
    }

    public void setFacetQueries(List<String> facetQueries) {
        this.facetQueries = facetQueries;
    }
}
