package com.adobe.aem.guides.wknd.core.schedulers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

/**
 * Unit tests for the ImportJsonFromAPIImpl class.
 * 
 * This test class uses JUnit 5 along with Mockito for mocking dependencies and 
 * TestLogger for capturing log events. The tests cover various scenarios including 
 * activation, running the scheduler, handling exceptions, and importing JSON data 
 * to CRX repository.
 */
@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
@MockitoSettings(strictness = Strictness.LENIENT)
public class ImportJsonFromAPIImplTest {

    private TestLogger logger = TestLoggerFactory.getTestLogger(ImportJsonFromAPIImpl.class);

    @Spy
    @InjectMocks
    ImportJsonFromAPIImpl importJsonFromAPI;

    @Mock
    private ImportJsonFromAPIImpl.Config config;

    @Mock
    private HttpURLConnection connection;

    @Mock
    private URL url;

    @Mock
    private InputStream inputStream;

    @Mock
    InputStreamReader inputStreamReader;

    @Mock
    JsonElement jsonElement;

    @Mock
    ResourceResolverFactory resolverFactory;

    @Mock
    ResourceResolver resolver;

    @Mock
    Resource parentResource;

    @Mock
    JsonObject jsonObject;

    @Mock
    JsonArray peopleArray;

    @Mock
    ModifiableValueMap personProperties;

   /*
    * Sets up the mock objects and configuration for each test.
    */
    @BeforeEach
    void setUp() {
        TestLoggerFactory.clear();
        when(config.scheduler_expression()).thenReturn("0 0/1 * 1/1 * ? *");
        when(config.api_url()).thenReturn("http://example.com/api");
        when(config.parent_node_path()).thenReturn("/content/wknd/data");
        when(config.enabled()).thenReturn(true);
        when(config.scheduler_concurrent()).thenReturn(false);

    }

    /*
     * Tests the activate method to ensure it correctly logs the activation details.
     */
    @Test
    void testActivate() {
        importJsonFromAPI.activate(config);
        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(1, events.size());
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals(3, events.get(0).getArguments().size());
        assertEquals("http://example.com/api", events.get(0).getArguments().get(0));
        assertEquals("/content/wknd/data", events.get(0).getArguments().get(1));
        assertEquals(true, events.get(0).getArguments().get(2));
    }

    /*
     * Tests the run method to ensure it correctly fetches JSON data from the API and logs the process.
     */
    @Test
    void run() {
        importJsonFromAPI.activate(config);
        String response = getJsonFromResources("People.json");
        List<LoggingEvent> events = null;
        doReturn(response).when(importJsonFromAPI).getJsonFromApi(anyString());
        importJsonFromAPI.run();
        events = logger.getLoggingEvents();
        assertEquals(true, config.enabled());
        assertEquals(6, events.size());
        assertEquals("ImportJsonFromAPIImpl Scheduler started", events.get(1).getMessage());
        assertEquals("ImportJsonFromAPIImpl Scheduler finished", events.get(5).getMessage());
    }

    /**
     * Tests the run method when the scheduler is disabled.
     */
    @Test
    void disabledRun() {
        when(config.enabled()).thenReturn(false);
        importJsonFromAPI.activate(config);
        importJsonFromAPI.run();
        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(false, config.enabled());
        assertEquals(2, events.size());
        assertEquals("ImportJsonFromAPIImpl is disabled", events.get(1).getMessage());
    }

    /*
     * Helper method to read JSON files from the resources folder.
     */
    private String getJsonFromResources(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            return new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + fileName, e);
        }
    }

    /*
     * Tests the getJsonFromApi method to ensure it correctly fetches JSON data from the API.
     */
    @Test
    void testGetJsonFromApi() {
        importJsonFromAPI.activate(config);
        String response = getJsonFromResources("People.json");
        String result = null;
        List<LoggingEvent> events = null;
        try {
            doReturn(url).when(importJsonFromAPI).getUrl(anyString());
            doReturn(connection).when(importJsonFromAPI).getHttpConnection(url);
            doNothing().when(connection).connect();
            when(connection.getInputStream()).thenReturn(inputStream);
            doReturn(inputStreamReader).when(importJsonFromAPI).getInputStreamReader(inputStream);
            doReturn(jsonElement).when(importJsonFromAPI).getJsonElement(inputStreamReader);
            when(jsonElement.toString()).thenReturn(response);

            result = importJsonFromAPI.getJsonFromApi("http://example.com/api");
            events = logger.getLoggingEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(response, result);
        assertEquals(3, events.size());
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals("Fetching JSON from API: {}http://example.com/api", events.get(1).getMessage() + config.api_url());
        assertEquals("Fetched JSON from API: {}" + response, events.get(2).getMessage() + result);
    }

    /*
     * Tests the getJsonFromApi method to handle exceptions when fetching JSON data from the API.
     */
    @Test
    void testGetJsonFromApiException() {
        importJsonFromAPI.activate(config);
        String result = null;
        List<LoggingEvent> events = null;
        try {
            doReturn(url).when(importJsonFromAPI).getUrl(anyString());
            doReturn(connection).when(importJsonFromAPI).getHttpConnection(url);
            doNothing().when(connection).connect();
            when(connection.getInputStream()).thenThrow(new IOException("Error fetching JSON from API"));

            result = importJsonFromAPI.getJsonFromApi("http://example.com/api");
            events = logger.getLoggingEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNull(result);
        assertEquals("Error fetching JSON from API", events.get(2).getMessage());
    }

    /*
     * Tests the deactivate method to ensure it correctly logs the deactivation details.
     */
    @Test
    void deactivate() {
        importJsonFromAPI.deactivate();
        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(1, events.size());
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals("ImportJsonFromAPIImpl deactivated", events.get(0).getMessage());
    }

    /*
     * Tests the getUrl method to ensure it correctly constructs a URL object.
     */
    @Test
    void testGetUrl() throws MalformedURLException {
        URL result = importJsonFromAPI.getUrl("http://example.com/api");
        assertEquals("http://example.com/api", result.toString());
    }

    /*
     * Tests the getHttpConnection method to ensure it correctly opens an HTTP connection.
     */
    @Test
    void testGetHttpConnection() throws IOException {
        doReturn(url).when(importJsonFromAPI).getUrl(anyString());
        when(url.openConnection()).thenReturn(connection);
        HttpURLConnection result = importJsonFromAPI.getHttpConnection(url);
        assertNotNull(result);
    }

    /*
     * Tests the getInputStreamReader method to ensure it correctly creates an InputStreamReader.
     */
    @Test
    void testGetInputStreamReader() {
        InputStreamReader result = importJsonFromAPI.getInputStreamReader(inputStream);
        assertNotNull(result);
    }

    /* Tests the importJsonToCrx method to handle null JSON data.
     * The method should log a debug message and skip the import process.
     */
    @Test
    void importNullJsonToCrx() {
        importJsonFromAPI.activate(config);
        List<LoggingEvent> events = null;
        doReturn(null).when(importJsonFromAPI).getJsonObject(anyString());
        importJsonFromAPI.importJsonToCrx(null, null);
        events = logger.getLoggingEvents();
        assertEquals(2, events.size());
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals("JSON is null, skipping import to CRX", events.get(1).getMessage());
    }

    /**
     * Tests the importJsonToCrx method when the parent node path does not exist.
     */
    @Test
    void importJsonToCrxParentNodeNull() {
        importJsonFromAPI.activate(config);
        List<LoggingEvent> events = null;
        try {
            String response = getJsonFromResources("People.json");
            doReturn(response).when(importJsonFromAPI).getJsonFromApi(anyString());
            when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resolver);
            when(resolver.getResource(anyString())).thenReturn(null);
            doReturn(jsonObject).when(importJsonFromAPI).getJsonObject(anyString());
            doReturn(peopleArray).when(jsonObject).getAsJsonArray(anyString());

            importJsonFromAPI.importJsonToCrx(response, response);

            events = logger.getLoggingEvents();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        assertEquals(3, events.size());
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals("Parent node path does not exist: {}", events.get(2).getMessage());
    }

    /**
     * Tests the importJsonToCrx method to ensure it correctly imports JSON 
     * data to the CRX repository.
     */
    @Test
    void importJsonToCrx() {
        importJsonFromAPI.activate(config);
        List<LoggingEvent> events = null;
        String json = getJsonFromResources("People.json");
        JsonObject jsonObject = importJsonFromAPI.getJsonObject(json);
        JsonArray peopleArray = jsonObject.getAsJsonArray("people");
        try {
            doReturn(json).when(importJsonFromAPI).getJsonFromApi(anyString());
            when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resolver);
            when(resolver.getResource(anyString())).thenReturn(parentResource);
            when(parentResource.getChild(anyString())).thenReturn(parentResource);
            when(parentResource.adaptTo(ModifiableValueMap.class)).thenReturn(personProperties);
            importJsonFromAPI.importJsonToCrx(json, config.parent_node_path());
            events = logger.getLoggingEvents();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        assertEquals(3, events.size());
        assertEquals(1, peopleArray.size()); 
        assertEquals(Level.DEBUG, events.get(0).getLevel());
        assertEquals("Importing JSON to CRX at path: {}", events.get(1).getMessage());
        assertEquals("Completed importing JSON to CRX", events.get(2).getMessage());
    }

}
