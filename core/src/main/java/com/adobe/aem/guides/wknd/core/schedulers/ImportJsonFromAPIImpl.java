package com.adobe.aem.guides.wknd.core.schedulers;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 * A scheduled task to import JSON from an API and store it in CRX
 */
@Component(service = Runnable.class)
@Designate(ocd = ImportJsonFromAPIImpl.Config.class)
public class ImportJsonFromAPIImpl implements ImportJsonFromAPI, Runnable {

    @ObjectClassDefinition(name = "Import JSON from API")
    public @interface Config {

        @AttributeDefinition(name = "Enabled", description = "Enable or disable this task")
        boolean enabled() default true;

        @AttributeDefinition(name = "Cron Expression", description = "Cron expression to schedule the job")
        String scheduler_expression() default "0 0/1 * 1/1 * ? *"; // Default: every minute

        @AttributeDefinition(name = "Concurrent task", description = "Whether or not to schedule this task concurrently")
        boolean scheduler_concurrent() default false;

        @AttributeDefinition(name = "API URL", description = "URL of the API to import JSON from")
        String api_url() default "http://18.223.243.34/content/wknd/people.json";

        @AttributeDefinition(name = "CRX Path", description = "Path in CRX to import JSON in node format")
        String parent_node_path() default "/content/wknd/data";
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String apiUrl;
    private String crxPath;
    private boolean enabled;

    /**
     * Activate this component
     * 
     * @param config Configuration object
     */
    @Activate
    protected void activate(final Config config) {
        apiUrl = config.api_url();
        crxPath = config.parent_node_path();
        enabled = config.enabled();
        logger.debug("ImportJsonFromAPIImpl activated with API URL: {}, crxPath: {}, enabled: {}", apiUrl, crxPath,
                enabled);
    
    }

    /**
     * Deactivate this component
     */
    @Deactivate
    protected void deactivate() {
        logger.debug("ImportJsonFromAPIImpl deactivated");
    }

    /**
     * Run the task
     */
    @Override
    public void run() {
        if (!enabled) {
            logger.debug("ImportJsonFromAPIImpl is disabled");
            return;
        }
        logger.debug("ImportJsonFromAPIImpl Scheduler started");
        String json = getJsonFromApi(apiUrl);
        importJsonToCrx(json, crxPath);
        logger.debug("ImportJsonFromAPIImpl Scheduler finished");
    }

    /**
     * Get JSON from API
     * 
     * @param apiUrl URL of the API to fetch JSON from
     * @return JSON string
     */
    @Override
    public String getJsonFromApi(String apiUrl) {
        try {
            logger.debug("Fetching JSON from API: {}", apiUrl);
            URL url = getUrl(apiUrl);
            HttpURLConnection httpConnection = getHttpConnection(url);
            httpConnection.connect();
            InputStream inputStream = httpConnection.getInputStream();
            InputStreamReader inputStreamReader = getInputStreamReader(inputStream);
            JsonElement jsonElement = getJsonElement(inputStreamReader);
            logger.debug("Fetched JSON from API: {}", jsonElement.toString());
            return jsonElement.toString();
        } catch (IOException e) {
            logger.error("Error fetching JSON from API", e);
            return null;
        }
    }

    /**
     * Get JSON element from input stream reader
     * 
     * @param inputStreamReader Input stream reader
     * @return JSON element
     */
    protected JsonElement getJsonElement(InputStreamReader inputStreamReader) {
        return JsonParser.parseReader(inputStreamReader);
    }

    /**
     * Get input stream reader from input stream
     * 
     * @param inputStream Input stream
     * @return Input stream reader
     */
    protected InputStreamReader getInputStreamReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return inputStreamReader;
    }

    /**
     * Get HTTP connection
     * 
     * @param url URL to connect to
     * @return HTTP connection
     * @throws IOException
     */
    protected HttpURLConnection getHttpConnection(URL url) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        return httpConnection;
    }

    /**
     * Get URL object from API URL
     * 
     * @param apiUrl API URL
     * @return URL object
     * @throws MalformedURLException
     */
    protected URL getUrl(String apiUrl) throws MalformedURLException {
        URL url = new URL(apiUrl);
        return url;
    }

    
    /**
     * Import JSON to CRX
     * 
     * @param json JSON string to import
     * @param crxPath Path in CRX to import JSON
     */
    @Override
    public void importJsonToCrx(String json, String crxPath) {
        if (json != null) {
            logger.debug("Importing JSON to CRX at path: {}", crxPath);

            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");

            try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {
                Resource parentResource = resolver.getResource(crxPath);
                if (parentResource == null) {
                    logger.error("Parent node path does not exist: {}", crxPath);
                    return;
                }

                JsonObject jsonObject = getJsonObject(json);
                JsonArray peopleArray = jsonObject.getAsJsonArray("people");

                for (JsonElement personElement : peopleArray) {
                    JsonObject personObject = personElement.getAsJsonObject();
                    String name = personObject.get("name").getAsString();

                    Resource personResource = parentResource.getChild(name);
                    if (personResource == null) {
                        logger.debug("Creating person node: {}", name);
                        personResource = resolver.create(parentResource, name, new HashMap<>());
                    }

                    ModifiableValueMap personProperties = personResource.adaptTo(ModifiableValueMap.class);
                    personProperties.put("name", name);
                    personProperties.put("age", personObject.get("age").getAsInt());
                    personProperties.put("email", personObject.get("email").getAsString());
                    personProperties.put("summaryBiography", personObject.get("summaryBiography").getAsString());
                    

                    JsonObject addressObject = personObject.getAsJsonObject("address");
                    Resource addressResource = personResource.getChild("address");
                    if (addressResource == null) {
                        addressResource = resolver.create(personResource, "address", new HashMap<>());
                    }
                    ModifiableValueMap addressProperties = addressResource.adaptTo(ModifiableValueMap.class);
                    addressProperties.put("street", addressObject.get("street").getAsString());
                    addressProperties.put("city", addressObject.get("city").getAsString());
                    addressProperties.put("zip", addressObject.get("zip").getAsString());

                    JsonArray phoneArray = personObject.getAsJsonArray("phoneNumber");
                    Resource phoneResource = personResource.getChild("phoneNumber");
                    if (phoneResource == null) {
                        phoneResource = resolver.create(personResource, "phoneNumber", new HashMap<>());
                    }

                    for (JsonElement phoneElement : phoneArray) {
                        JsonObject phoneObject = phoneElement.getAsJsonObject();
                        String phoneType = phoneObject.get("type").getAsString();
                        String phoneNumber = phoneObject.get("number").getAsString();

                        // Create a unique node for each phone number
                        Resource individualPhoneResource = phoneResource.getChild(phoneType);
                        if(individualPhoneResource == null) {
                            individualPhoneResource = resolver.create(phoneResource, phoneType, new HashMap<>());
                        }
                        ModifiableValueMap individualPhoneProperties = individualPhoneResource
                                .adaptTo(ModifiableValueMap.class);
                        individualPhoneProperties.put("type", phoneType);
                        individualPhoneProperties.put("number", phoneNumber);
                    }

                    JsonArray occupationsArray = personObject.getAsJsonArray("Occupations");
                    Resource occupationsResource = personResource.getChild("Occupations");
                    if (occupationsResource == null) {
                        occupationsResource = resolver.create(personResource, "Occupations", new HashMap<>());
                    }

                    for (JsonElement occupationElement : occupationsArray) {
                        String occupation = occupationElement.getAsString();
                        Resource occupationResource = occupationsResource.getChild(occupation);
                        if (occupationResource == null) {
                            occupationResource = resolver.create(occupationsResource, occupation, new HashMap<>());
                        }
                        ModifiableValueMap occupationProperties = occupationResource.adaptTo(ModifiableValueMap.class);
                        occupationProperties.put("occupation", occupation);
                    }

                }

                resolver.commit();
            } catch (PersistenceException e) {
                logger.error("Error committing changes to CRX", e);
            } catch (Exception e) {
                logger.error("Error importing JSON to CRX", e);
            }
            logger.debug("Completed importing JSON to CRX");
            return;
        } else {
            logger.error("JSON is null, skipping import to CRX");
        }
    }

    protected JsonObject getJsonObject(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
