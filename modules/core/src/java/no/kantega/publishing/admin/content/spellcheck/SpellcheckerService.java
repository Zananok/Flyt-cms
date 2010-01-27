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

/**
 *
 */
public interface SpellcheckerService {


    /**
     * Checks a list of words for misspelled words. Returns a list containing the words believed to be misspelled.
     *
     * @param words a list of words to check.
     * @return a list of misspelled words.
     */
    public List<String> spellcheck(List<String> words);

    /**
     * Suggests a set of alternatives for a misspelled word.
     *
     * @param word a misspelled word.
     * @return a list of suggestions.
     */
    public List<String> suggest(String word);

}
