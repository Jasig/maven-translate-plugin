package org.jasig.i18n.translate;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testContext.xml")
public class MessageFileServiceTest {

    MessageFileService messageFileService;
    Resource messageFile;
    
    @Autowired
    ApplicationContext applicationContext;
    
    @Before
    public void setUp() {
        messageFileService = new MessageFileService();
        messageFile = applicationContext.getResource("classpath:/i18n/Messages.properties");
    }
    
    @Test
    public void testGetMessageKeys() throws IOException {
        Set<String> keys = messageFileService.getMessageKeysFromFile(messageFile);
        assertEquals(3, keys.size());
    }
    
    @Test
    public void testGetMessageMap() throws IOException {
        Map<String, String> keys = messageFileService.getMessageMapFromFile(messageFile);
        assertEquals(3, keys.size());
        assertEquals("Third Message", keys.get("key3"));
    }
    
}
