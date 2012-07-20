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

package no.kantega.search.query;

import no.kantega.search.criteria.Criterion;

import java.util.List;

/**
 * Date: Dec 3, 2008
 * Time: 7:49:37 AM
 *
 * @author Tarje Killingberg
 */
public interface SearchQuery {


    /**
     * Returnerer en liste med Criterion-objekter som representerer dette søket.
     * 
     * @return en liste med Criterion-objekter.
     */
    public List<Criterion> getCriteria();

    /**
     * Returnerer en liste med Criterion-objekter som skal benyttes som filter for dette søket.
     *
     * @return en liste med Criterion-objekter som skal benyttes som filter for dette søket
     */
    public List<Criterion> getFilterCriteria();

    /**
     * Returnerer det maksimale antallet treff som skal returneres for dette søket.
     * @return det maksimale antallet treff som skal returneres for dette søket
     */
    public int getMaxHits();

    /**
     * Returns the sort that should be applied on the results returned
     * from this search query. May return <tt>null</tt> if default sort
     * should be used.
     *
     * @return the sort that should be applied.
     */
    public Sort getSort();

    public enum Sort {
        ASCENDING, DESCENDING
    }
}
