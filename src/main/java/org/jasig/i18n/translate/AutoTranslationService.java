package org.jasig.i18n.translate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class AutoTranslationService {
        
    public String getTranslationMessages(Resource mainMessagesFile, Resource languageMessagesFile, Language language) throws IOException {
        
        InputStream inputStream = mainMessagesFile.getInputStream();
        Reader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);

        Map<String,String> translatedMessages = new TreeMap<String,String>();        

        String line;
        try {
            
            InputStream inputStream2 = languageMessagesFile.getInputStream();
            Reader inputReader2 = new InputStreamReader(inputStream2);
            BufferedReader bufferedReader2 = new BufferedReader(inputReader2);

            /*
             *  Parse the existing language file and build a map of key-value message
             *  pairs.
             */
            while ((line = bufferedReader2.readLine()) != null) {
                String[] s = line.split("=", 2);
                if (s.length == 2) {
                    translatedMessages.put(s[0], s[1]);
                }
            }
            
        } catch (Exception e) {
            System.out.println("No file found");
            System.out.println(e);
        }

        /*
         *  Parse the main messages file and identify any message keys missing in
         *  the language-specific file. For each missing message, add the key
         *  to our translation key list and add the value as a new line in the
         *  to-be-translated StringBuffer
         */
        List<String> keys = new ArrayList<String>();
        while ((line = bufferedReader.readLine()) != null) {
            String[] s = line.split("=", 2);
            if (s.length == 2 && !translatedMessages.containsKey(s[0])) {
                keys.add(s[0]);
                translatedMessages.put(s[0], translateMessage(s[1], language));
            }
        }

        // write out the translated message keypairs in message file format
        StringBuffer translated = new StringBuffer();
        for (Map.Entry<String, String> entry : translatedMessages.entrySet()) {
            translated.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        
        return translated.toString();
    }
    
    public String translateMessage(String untranslated, Language language) {
        // Set the HTTP referrer to your website address.
        Translate.setHttpReferrer("http://localhost:8080/uPortal");

        try {
            String translated = Translate.execute(untranslated, Language.ENGLISH, language);
            return translated;
        } catch (Exception e) {
            System.out.println(e);
        }
        return untranslated;
    }
    
}
