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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.api.translate.Language;

/**
 * Goal which touches a timestamp file.
 *
 * @goal translate
 * 
 * @phase process-sources
 */
public class MyMojo
    extends AbstractMojo
{

    /**
     * Output location for the generated NOTICE file
     *
     * @parameter default-value="${basedir}/src/main/resources/properties/i18n/"
     */
    protected String outputDirectory = "";
    
    protected String mainMessagesFile = "Messages.properties";
    
    protected Map<String, Language> languages;
    
    protected AutoTranslationService translationService;
    
    public MyMojo() {
        languages = new HashMap<String, Language>();
        languages.put("es", Language.SPANISH);
        languages.put("ja", Language.JAPANESE);
        languages.put("fr", Language.FRENCH);
        languages.put("de", Language.GERMAN);
        languages.put("mk", Language.MACEDONIAN);
        languages.put("sv", Language.SWEDISH);
        languages.put("lv", Language.LATVIAN);
        
        translationService = new AutoTranslationService();
    }

    public void execute()
        throws MojoExecutionException
    {
        Resource resource = new FileSystemResource(outputDirectory + "Messages.properties");

        try {
            for (Map.Entry<String, Language> entry : languages.entrySet()) {
                String key = entry.getKey();
                Language lang = entry.getValue();
    
                System.out.println("starting translation for " + key);
                System.out.println("using file " + outputDirectory + "Messages_" + key + ".properties");
    
                Resource resource2 = new FileSystemResource(outputDirectory + "Messages_" + key + ".properties");
                if (resource2 == null) {
                    System.out.println("Using blank");
                    resource2 = new FileSystemResource(outputDirectory + "blank.properties");
                } else { 
                    System.out.println("Found file");
                }
                String translated = translationService.getTranslationMessages(resource, resource2, lang);
                System.out.println("writing to file");
                java.text.DecimalFormat f;
                f = new java.text.DecimalFormat();
                f.applyPattern("\\u0000");
    
                File file = new File(outputDirectory + "Messages_" + key + ".properties");
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter out = new OutputStreamWriter(fos); 
                
                char[] chars = new char[translated.length()];
                translated.getChars(0, chars.length, chars, 0);
                for (char c : chars) {
                    if (c > '\u007f') {
                        out.write("\\u");
                        out.write(UnicodeFormatter.charToHex(c));
                    } else {
                        out.write(c);
                    }
                }
                
                out.flush();
                out.close();
            }
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }
}
