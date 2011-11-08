/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.i18n.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.i18n.translate.GoogleTranslationResponse.Translation;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class AutoTranslationService {
    
    protected final Log log = LogFactory.getLog(getClass());

    private String urlTemplate = "https://www.googleapis.com/language/translate/v2?key={key}&source={source}&target={target}&q={query}";
    
    private String apiKey;
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    private String defaultLanguageKey;
    
    public void setDefaultLanguageKey(String defaultLanguageKey) {
        this.defaultLanguageKey = defaultLanguageKey;
    }
    
    private RestTemplate restTemplate;
    
    public AutoTranslationService() {
        this.restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = Collections.<HttpMessageConverter<?>>singletonList(new MappingJacksonHttpMessageConverter());
        this.restTemplate.setMessageConverters(converters);
    }
    
    public Map<String,String> getAutoUpdatedTranslationMap(Map<String, String> mainMap, Map<String, String> targetMap, String languageKey) {

        // assemble a set of keys represented in the main map but not in the
        // target language map
        Set<String> missing = mainMap.keySet();
        missing.removeAll(targetMap.keySet());
        
        // put the keys in a list so that we have a consistent ordering
        List<String> keys = new ArrayList<String>();
        keys.addAll(missing);

        // assemble a list of untranslated messages in the same order as our
        // missing key list
        List<String> untranslatedMessages = new ArrayList<String>();
        for (String key : keys) {
            untranslatedMessages.add(mainMap.get(key));
        }
                
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("key", this.apiKey);
        parameters.put("source", this.defaultLanguageKey);
        parameters.put("target", languageKey);
        parameters.put("query", untranslatedMessages);
        
        GoogleTranslationResponse response = this.restTemplate.getForObject(urlTemplate, GoogleTranslationResponse.class, parameters);        
        List<Translation> translations = response.getTranslations();
        
        // iterate through the auto-translations, adding each to the target
        // map
        ListIterator<Translation> iter = translations.listIterator();
        while (iter.hasNext()) {
            Translation translation = iter.next();
            String key = keys.get(iter.previousIndex());
            targetMap.put(key, translation.getTranslatedText());
        }
        
        return targetMap;
    }
    
}
