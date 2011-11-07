package org.jasig.i18n.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

public class MessageFileService {

    protected final Log log = LogFactory.getLog(getClass());

    public Set<String> getMessageKeysFromFile(Resource messageFile) {
        final Set<String> keys = new HashSet<String>();
        
        try {
            
            final InputStream inputStream = messageFile.getInputStream();
            final Reader inputReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputReader);
    
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] s = line.split("=", 2);
                if (s.length == 2) {
                    keys.add(s[0]);
                }
            }
            
        } catch (IOException ex) {
            log.error("IOException while parsing messages file", ex);
        }
        
        return keys;
        
    }
    
    public Map<String,String> getMessageMapFromFile(Resource messageFile) {
        final Map<String, String> map = new HashMap<String, String>();
        
        try {
            
            final InputStream inputStream = messageFile.getInputStream();
            final Reader inputReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputReader);
    
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] s = line.split("=", 2);
                if (s.length == 2) {
                    map.put(s[0], s[1]);
                }
            }
            
        } catch (IOException ex) {
            log.error("IOException while parsing messages file", ex);
        }
        
        return map;
        
    }
    
}
