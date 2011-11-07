package org.jasig.i18n.translate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.api.translate.Language;

/**
 * Goal which reports missing message keys for a set of target languages.
 *
 * @goal check-message-keys
 * 
 * @phase process-sources
 */
public class ReportMissingKeysMojo extends AbstractMojo {

    /**
     * Messages directory location
     *
     * @parameter default-value="${basedir}/src/main/resources/properties/i18n/"
     */
    protected String messagesDirectory = "";
    
    protected String mainMessagesFile = "Messages.properties";
    
    protected Map<String, Language> languages;
    
    protected MessageFileService messageFileService;
    
    public ReportMissingKeysMojo() {
        languages = new HashMap<String, Language>();
        languages.put("es", Language.SPANISH);
        languages.put("ja", Language.JAPANESE);
        languages.put("fr", Language.FRENCH);
        languages.put("de", Language.GERMAN);
        languages.put("mk", Language.MACEDONIAN);
        languages.put("sv", Language.SWEDISH);
        languages.put("lv", Language.LATVIAN);
        
        messageFileService = new MessageFileService();
    }

    public void execute() throws MojoExecutionException {

        // parse the main messages file and create a set of its defined 
        // message keys
        Resource resource = new FileSystemResource(messagesDirectory + "Messages.properties");
        Set<String> mainKeys = messageFileService.getMessageKeysFromFile(resource);

        // for each configured language, check the keys in the language file 
        // against those in the main file
        for (Map.Entry<String, Language> entry : languages.entrySet()) {
            String key = entry.getKey();
            Language lang = entry.getValue();

            try {

                // parse the target language messages file and collect a set of
                // defined keys
                Resource resource2 = new FileSystemResource(messagesDirectory + "Messages_" + key + ".properties");
                Set<String> targetKeys = messageFileService.getMessageKeysFromFile(resource2);

                // create a set of any keys that are in the main message file,
                // but not in the target file
                Set<String> missingKeys = new HashSet<String>();
                missingKeys.addAll(mainKeys);
                missingKeys.removeAll(targetKeys);
                
                // report any missing keys for the current language
                if (missingKeys.size() > 0) {
                    System.out.println("Found " + missingKeys.size() + " missing keys for " + lang.name() + "(" + missingKeys.toString() + ")");
                }
                
            } catch (Exception ex) {
                System.out.println("Messages file for language '" + key + "' (" + lang.name() + ") cannot be located");
                return;
            }
        }
        
    }

}
