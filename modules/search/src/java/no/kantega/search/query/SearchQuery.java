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

import no.kantega.search.core.SearchHandler;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.index.IndexManager;

import java.util.List;

/**
 * Date: Dec 3, 2008
 * Time: 7:49:37 AM
 *
 * @author Tarje Killingberg
 */
public interface SearchQuery {


    /**
     * Returnerer en liste med Criterion-objekter som representerer dette s�ket.
     * 
     * @return en liste med Criterion-objekter.
     */
    public List<Criterion> getCriteria();

    /**
     * Returnerer en liste med Criterion-objekter som skal benyttes som filter for dette s�ket.
     *
     * @return en liste med Criterion-objekter som skal benyttes som filter for dette s�ket
     */
    public List<Criterion> getFilterCriteria();

    /**
     * Returnerer en instans av en klasse som implementerer SearchHandler som kan brukes til � utf�re s�k p�
     * dette SearchQuery'et. Denne instansen m� v�re ferdig initialisert og klar til � brukes.
     *
     * @param indexManager et IndexManager-objekt
     * @return en instans av en klasse som implementerer SearchHandler
     */
    public SearchHandler getSearchHandler(IndexManager indexManager);

    /**
     * Returnerer det maksimale antallet treff som skal returneres for dette s�ket.
     * @return det maksimale antallet treff som skal returneres for dette s�ket
     */
    public int getMaxHits();

}
