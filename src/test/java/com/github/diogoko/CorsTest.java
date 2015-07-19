package com.github.diogoko;

import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.applet.AppletDescription;
import com.github.diogoko.applet.AppletInstance;
import com.github.diogoko.rest.AppletsResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CorsTest extends JerseyTest {
    private AppletContainer container;

    @Override
    protected Application configure() {
        AppletDescription desc = new AppletDescription("test");
        AppletInstance applet = mock(AppletInstance.class);
        when(applet.getDescription()).thenReturn(desc);

        container = mock(AppletContainer.class);
        when(container.findByName("test")).thenReturn(applet);

        Options options = new Options();
        options.setAllowOrigin(new HashSet<>(Arrays.asList("http://test.org", "http://sample.com")));
        return new AppletsResourceConfig(container, options);
    }

    @Test
    public void noOrigin() {
        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Access-Control-Allow-Origin"));
        assertNull(response.getHeaderString("Access-Control-Allow-Methods"));
        assertNull(response.getHeaderString("Access-Control-Allow-Headers"));
    }

    @Test
    public void wrongOrigin() {
        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Origin", "http://samplezz.com")
                .get();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Access-Control-Allow-Origin"));
        assertNull(response.getHeaderString("Access-Control-Allow-Methods"));
        assertNull(response.getHeaderString("Access-Control-Allow-Headers"));
    }

    @Test
    public void correctRequest() {
        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Origin", "http://sample.com")
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("http://sample.com", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, PUT, DELETE", response.getHeaderString("Access-Control-Allow-Methods"));
        assertEquals("Content-Type", response.getHeaderString("Access-Control-Allow-Headers"));
    }

    @Test
    public void optionsIncorrectRequest() {
        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Origin", "http://samplezz.com")
                .options();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Access-Control-Allow-Origin"));
        assertNull(response.getHeaderString("Access-Control-Allow-Methods"));
        assertNull(response.getHeaderString("Access-Control-Allow-Headers"));
    }

    @Test
    public void optionsCorrectRequest() {
        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Origin", "http://sample.com")
                .options();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("http://sample.com", response.getHeaderString("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, PUT, DELETE", response.getHeaderString("Access-Control-Allow-Methods"));
        assertEquals("Content-Type", response.getHeaderString("Access-Control-Allow-Headers"));
    }
}
