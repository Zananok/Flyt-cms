/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.spellcheck;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SpellcheckerService {


    /**
     * Checks a list of words for misspelled words. Returns a list containing the words believed to be misspelled.
     *
     * @param words a list of words to check.
     * @param lang
     * @return a list of misspelled words.
     */
    public List<String> spellcheck(List<String> words, String lang);

    /**
     * Suggests a set of alternatives for a misspelled word.
     *
     * @param word a misspelled word.
     * @param lang
     * @return a list of suggestions.
     */
    public List<String> suggest(String word, String lang); // ta inn spr�k eller spellchecker


    /**
     * Returns a list of SpellcheckerInfo-objects, one for each Spellchecker known to this SpellcheckerService.
     *
     * @return a list of SpellcheckerInfo-objects
     */
    public Map<String, SpellcheckerInfo> getSpellCheckers();

}
