package org.jasig.i18n.translate;

import java.util.List;

public class GoogleTranslationResponse {

    private GoogleResponseData data;
    
    public List<Translation> getTranslations() {
        return this.data.translations;
    }
    
    public GoogleResponseData getData() {
        return data;
    }

    public void setData(GoogleResponseData data) {
        this.data = data;
    }

    public class GoogleResponseData {
        private List<Translation> translations;

        public List<Translation> getTranslations() {
            return translations;
        }

        public void setTranslations(List<Translation> translations) {
            this.translations = translations;
        }
        
    }
    
    public class Translation {

        private String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }

        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }
        
        
    }

}
