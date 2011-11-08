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

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Goal which updates a target language file with automatic translations of
 * any missing keys.
 *
 * @goal translate
 * 
 * @phase process-sources
 */
public class AutoTranslateMojo extends AbstractMojo {

    /**
     * Messages directory location
     *
     * @parameter default-value="${basedir}/src/main/resources/properties/i18n/"
     */
    private String messagesDirectory;

    /**
     * @parameter default-value="Messages.properties"
     */
    private String mainMessagesFile;

    /**
     * @parameter default-value="es,ja,fr,de,mk,sv,lv"
     */
    private List<String> languageKeys;
    
    private AutoTranslationService translationService;

    private MessageFileService messageFileService;
    
    public AutoTranslateMojo() {        
        translationService = new AutoTranslationService();
        messageFileService = new MessageFileService();
    }

    public void execute() throws MojoExecutionException {

        Map<String, String> mainMap = null;
        
        try {
            // parse the main messages file and create a set of its defined 
            // message keys
            Resource resource = new FileSystemResource(messagesDirectory + mainMessagesFile);
            mainMap = messageFileService.getMessageMapFromFile(resource);
        } catch (IOException ex) {
            System.out.println("Main messages file could not be located");
        }

        // update each language file, setting the values for any missing keys
        // to an auto-translated version of the default language message
        for (String key : languageKeys) {

            try {

                // parse the target language messages file and collect a set of
                // defined keys
                Resource languageFile = new FileSystemResource(messagesDirectory + "Messages_" + key + ".properties");
                Map<String, String> targetMap = messageFileService.getMessageMapFromFile(languageFile);

                Map<String, String> updatedMap = translationService.getAutoUpdatedTranslationMap(mainMap, targetMap, key);
                
                messageFileService.updateMessageFile(languageFile, updatedMap);
                
            } catch (Exception ex) {
                System.out.println("Messages file for language '" + key + "' (" + key + ") cannot be located");
                return;
            }
        }

    }
}
