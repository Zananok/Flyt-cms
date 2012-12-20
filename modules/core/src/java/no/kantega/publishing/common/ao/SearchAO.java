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
import no.kantega.publishing.common.data.SearchResult;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.search.index.Fields;
import no.kantega.search.index.IndexSearcherManager;
import no.kantega.search.analysis.AnalyzerFactory;
import no.kantega.publishing.spring.RootContext;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchAO {
    private static final String SOURCE = "aksess.SearchAO";
    private static Logger log  = Logger.getLogger(SearchAO.class);

    public static List search(String phrase) throws SystemException {

        ApplicationContext applicationContext = RootContext.getInstance();

        Analyzer analyzer = ((AnalyzerFactory)applicationContext.getBean("analyzerFactory")).createInstance();

        IndexSearcherManager manager = (IndexSearcherManager) applicationContext.getBean("indexSearcherManager");

        try {
            IndexSearcher searcher = manager.getSearcher("aksess");

            BooleanQuery query = new BooleanQuery();

            QueryParser parser = new QueryParser(Fields.CONTENT, analyzer);

            query.add(parser.parse(phrase), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term(Fields.DOCTYPE, Fields.TYPE_CONTENT)), BooleanClause.Occur.MUST);

            Hits hits = searcher.search(query);

            List results = new ArrayList();
            for(int i = 0; i < hits.length() && i < 100; i++) {
                try {
                    Document doc = hits.doc(i);
                    int contentId =  Integer.parseInt(doc.get(Fields.CONTENT_ID));
                    int contentStatus =  Integer.parseInt(doc.get(Fields.CONTENT_STATUS));
                    String title = doc.get(Fields.TITLE);
                    Date lastModified = DateTools.stringToDate(doc.get(Fields.LAST_MODIFIED));

                    results.add(new SearchResult(contentId, contentStatus, title, null,  lastModified));
                } catch (IOException e) {
                    log.error(e);
                } catch (ParseException e) {
                    log.error(e);
                }
            }
            return results;
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        } catch (org.apache.lucene.queryParser.ParseException e) {
            log.error(e);
            throw new RuntimeException(e);
        }

    }

    public static void registerSearch(String queryString, String exactQuery, int siteId, int numberOfHits) {
        JdbcTemplate temp = getJdbcTemplate();

        temp.update("insert into searchlog (Time, Query, ExactQuery, NumberOfHits, SiteId) VALUES (?,?,?,?,?)", new Object[] {
                new Timestamp(new java.util.Date().getTime()),
                queryString,
                exactQuery,
                numberOfHits,
                siteId
        });
    }

    public static int getSearchCountForPeriod(Date after, Date before, int siteId) {
        JdbcTemplate template = getJdbcTemplate();
        StringBuffer buffer = new StringBuffer();

        List objects = new ArrayList();

        objects.add(siteId);
        buffer.append("where siteId=?");

        if(after!= null) {
            objects.add(new Timestamp(after.getTime()));
            buffer.append(" and Time >= ? ");

        }
        if(before != null) {
            objects.add(new Timestamp(before.getTime()));

            buffer.append(" and Time <= ? ");

        }
        return template.queryForInt("select count(*) from searchlog " + buffer.toString(), objects.toArray(new Object[0]));
    }

    public static List getMostPopularQueries(int siteId) {
        return getQueryStats("numberofsearches desc", siteId);
    }

    public static List getQueriesWithLeastHits(final int siteId) {
        return getQueryStats("numberofhits asc", siteId);
    }

    private static List getQueryStats(final String orderBy, final int siteId) {

        return getJdbcTemplate().query(new PreparedStatementCreator() {

            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement p = connection.prepareStatement("select query, count(*) as numberofsearches, avg(numberofhits) as numberofhits from searchlog where siteId=? group by query order by " + orderBy);
                p.setMaxRows(100);
                p.setInt(1, siteId);
                return p;
            }
        }, new RowMapper() {

            public Object mapRow(ResultSet rs, int i) throws SQLException {
                return new QueryStatItem(rs.getString("query"), rs.getInt("numberofsearches"), rs.getDouble("numberofhits"));
            }
        });
    }

    private static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dbConnectionFactory.getDataSource());
    }

    public static class QueryStatItem {

        private String query;
        private int numberOfSearches;
        private double numberOfHits;

        public QueryStatItem(String query, int numberOfSearches, double numberOfHits) {
            this.query = query;
            this.numberOfSearches = numberOfSearches;
            this.numberOfHits = numberOfHits;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getNumberOfSearches() {
            return numberOfSearches;
        }

        public void setNumberOfSearches(int numberOfSearches) {
            this.numberOfSearches = numberOfSearches;
        }

        public double getNumberOfHits() {
            return numberOfHits;
        }

        public void setNumberOfHits(int numberOfHits) {
            this.numberOfHits = numberOfHits;
        }
    }
}