package org.apache.lucene.analysis.miscellaneous;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.util.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.KeepWordFilter;

import java.util.Map;
import java.util.Set;
import java.io.IOException;

/**
 * Factory for {@link KeepWordFilter}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text_keepword" class="solr.TextField" positionIncrementGap="100"&gt;
 *   &lt;analyzer&gt;
 *     &lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;
 *     &lt;filter class="solr.KeepWordFilterFactory" words="keepwords.txt" ignoreCase="false" enablePositionIncrements="false"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 *
 */
public class KeepWordFilterFactory extends TokenFilterFactory implements ResourceLoaderAware {

  @Override
  public void init(Map<String,String> args) {
    super.init(args);
    assureMatchVersion();
  }

  @Override
  public void inform(ResourceLoader loader) throws IOException {
    String wordFiles = args.get("words");
    ignoreCase = getBoolean("ignoreCase", false);
    enablePositionIncrements = getBoolean("enablePositionIncrements",false);

    if (wordFiles != null) {
      words = getWordSet(loader, wordFiles, ignoreCase);
    }
  }

  private CharArraySet words;
  private boolean ignoreCase;
  private boolean enablePositionIncrements;

  /**
   * Set the keep word list.
   * NOTE: if ignoreCase==true, the words are expected to be lowercase
   */
  public void setWords(Set<String> words) {
    this.words = new CharArraySet(luceneMatchVersion, words, ignoreCase);
  }

  public void setIgnoreCase(boolean ignoreCase) {    
    if (words != null && this.ignoreCase != ignoreCase) {
      words = new CharArraySet(luceneMatchVersion, words, ignoreCase);
    }
    this.ignoreCase = ignoreCase;
  }

  public boolean isEnablePositionIncrements() {
    return enablePositionIncrements;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public CharArraySet getWords() {
    return words;
  }

  @Override
  public TokenStream create(TokenStream input) {
    // if the set is null, it means it was empty
    return words == null ? input : new KeepWordFilter(enablePositionIncrements, input, words);
  }
}
