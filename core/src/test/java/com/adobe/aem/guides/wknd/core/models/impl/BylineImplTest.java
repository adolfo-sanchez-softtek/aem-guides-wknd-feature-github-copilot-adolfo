package com.adobe.aem.guides.wknd.core.models.impl;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.adobe.aem.guides.wknd.core.models.Byline;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.aem.guides.wknd.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class BylineImplTest {

    private final AemContext ctx = AppAemContext.newAemContext();

    @Mock
    private Image image;

    @Mock
    private ModelFactory modelFactory;

    @BeforeEach
    void setUp() throws Exception {
        ctx.addModelsForClasses(BylineImpl.class);
        ctx.load().json("/BylineImplTest.json", "/content");
        
        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()), any(Resource.class), eq(Image.class)))
                .thenReturn(image);

        ctx.registerService(ModelFactory.class, modelFactory, org.osgi.framework.Constants.SERVICE_RANKING,
                Integer.MAX_VALUE);
    }

    @Test
    void testGetName() {
        final String expected = "Jane Doe";
        ctx.currentResource("/content/byline");
        Byline byline = ctx.request().adaptTo(Byline.class);
        String actual = byline.getName();
        assertEquals(expected, actual);
    }

    @Test
    void testGetName_Empty() {
        ctx.currentResource("/content/without-name");
        Byline byline = ctx.request().adaptTo(Byline.class);
        assertNull(byline.getName());
    }

    @Test
    void testGetOccupations() {
        final String[] expected = { "Blogger", "Photographer", "YouTuber" };
        ctx.currentResource("/content/byline");
        Byline byline = ctx.request().adaptTo(Byline.class);
        String[] actual = byline.getOccupations().toArray(new String[0]);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testGetOccupations_Empty() {
        ctx.currentResource("/content/without-occupations");
        Byline byline = ctx.request().adaptTo(Byline.class);
        assertTrue(byline.getOccupations().isEmpty());
    }

    @Test
    void testIsEmpty() {
        ctx.currentResource("/content/byline");
        Byline byline = ctx.request().adaptTo(Byline.class);
        assertTrue(byline.isEmpty());
    }

    @Test
    void testIsEmpty_Empty() {
        ctx.currentResource("/content/empty");
        Byline byline = ctx.request().adaptTo(Byline.class);
        assertTrue(byline.isEmpty());
    }

    @Test
    public void testIsEmpty_WithoutName() {
        ctx.currentResource("/content/without-name");

        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    public void testIsEmpty_WithoutOccupations() {
        ctx.currentResource("/content/without-occupations");

        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    public void testIsEmpty_WithoutImage() {
        ctx.currentResource("/content/byline");

        lenient().when(modelFactory.getModelFromWrappedRequest(eq(ctx.request()),
            any(Resource.class),
            eq(Image.class))).thenReturn(null);

        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    public void testIsEmpty_WithoutImageSrc() {
        ctx.currentResource("/content/byline");

        when(image.getSrc()).thenReturn("");

        Byline byline = ctx.request().adaptTo(Byline.class);

        assertTrue(byline.isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        ctx.currentResource("/content/byline");
        when(image.getSrc()).thenReturn("/content/bio.png");

        Byline byline = ctx.request().adaptTo(Byline.class);

        assertFalse(byline.isEmpty());
    }
}