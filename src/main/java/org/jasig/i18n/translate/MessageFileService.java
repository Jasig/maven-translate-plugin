package org.jasig.i18n.translate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    
    public void updateMessageFile(Resource messageFile, Map<String, String> messageMap) throws IOException {
        
        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, String> message : messageMap.entrySet()) {
            buf.append(message.getKey()).append("=").append(message.getValue());
        }
        String translated = buf.toString();
        
        java.text.DecimalFormat f;
        f = new java.text.DecimalFormat();
        f.applyPattern("\\u0000");

        File file = messageFile.getFile();
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

    public Set<String> getMessageKeysFromFile(Resource messageFile) throws IOException {
        final Set<String> keys = new HashSet<String>();
        
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
        
        return keys;
        
    }
    
    public Map<String,String> getMessageMapFromFile(Resource messageFile) throws IOException {
        final Map<String, String> map = new HashMap<String, String>();
        
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
        
        return map;
        
    }
    
}
