package com.github.diogoko;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestTest extends JerseyTest {

    private AppletContainer container;

    @Override
    protected Application configure() {
        container = mock(AppletContainer.class);
        return new AppletsResourceConfig(container);
    }

    @Test
    public void lifeCycle() throws URISyntaxException {
        AppletInstance applet = mock(AppletInstance.class);

        AppletDescription desc = new AppletDescription();
        desc.setName("test");
        CreateEvent startEvent = new CreateEvent();
        startEvent.setApplet(desc);
        startEvent.setShow(false);

        when(container.findByName("test")).thenReturn(null);
        when(container.createApplet(any(AppletDescription.class))).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);
        Response response = target("applets")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(startEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        URI location = new URI(response.getHeaderString("Location"));
        assertEquals(location.getPath(), "/applets/test");
    }
}
