package com.adobe.aem.guides.wknd.core.schedulers;

public interface ImportJsonFromAPI {

    public String getJsonFromApi(String apiUrl);

    public void importJsonToCrx(String json, String crxPath);
 
    
}
