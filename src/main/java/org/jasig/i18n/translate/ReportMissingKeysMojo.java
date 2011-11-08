package org.jasig.i18n.translate;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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
    private String messagesDirectory;

    /**
     * @parameter default-value="Messages.properties"
     */
    private String mainMessagesFile;

    /**
     * @parameter default-value="es,ja,fr,de,mk,sv,lv"
     */
    private List<String> languageKeys;
    
    private MessageFileService messageFileService;
    
    public ReportMissingKeysMojo() {
        messageFileService = new MessageFileService();
    }

    public void execute() throws MojoExecutionException {

        Set<String> mainKeys = null;
        
        try {
            // parse the main messages file and create a set of its defined 
            // message keys
            Resource resource = new FileSystemResource(messagesDirectory + mainMessagesFile);
            mainKeys = messageFileService.getMessageKeysFromFile(resource);
        } catch (IOException ex) {
            System.out.println("Main messages file could not be located");
        }

        // for each configured language, check the keys in the language file 
        // against those in the main file
        for (String key : languageKeys) {

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
                    System.out.println("Found " + missingKeys.size() + " missing keys for " + key + "(" + missingKeys.toString() + ")");
                }
                
            } catch (Exception ex) {
                System.out.println("Messages file for language '" + key + "' (" + key + ") cannot be located");
                return;
            }
        }
        
    }

}
