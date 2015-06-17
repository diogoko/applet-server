package com.github.diogoko;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestTest extends JerseyTest {

    private AppletContainer container;

    @Override
    protected Application configure() {
        container = mock(AppletContainer.class);
        return new AppletsResourceConfig(container);
    }

    @Test
    public void createNamed() throws URISyntaxException, IOException {
        AppletDescription desc = new AppletDescription("test");
        CreateEvent startEvent = new CreateEvent(desc, false);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(null);
        when(container.createApplet(any(AppletDescription.class))).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);

        Response response = target("applets")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(startEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        URI location = new URI(response.getHeaderString("Location"));
        assertEquals(location.getPath(), "/applets/test");
        AppletDescription returnedDesc = readDescription(response);
        assertEquals(returnedDesc.getName(), "test");
    }

    @Test
    public void createUnnamed() throws URISyntaxException, IOException {
        AppletDescription desc = new AppletDescription();
        CreateEvent startEvent = new CreateEvent(desc, false);

        String nameToGenerate = UUID.randomUUID().toString();
        AppletDescription descToReturn = new AppletDescription(nameToGenerate);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(null);
        when(container.createApplet(any(AppletDescription.class))).thenReturn(applet);
        when(applet.getDescription()).thenReturn(descToReturn);

        Response response = target("applets")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(startEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        URI location = new URI(response.getHeaderString("Location"));
        assertEquals(location.getPath(), "/applets/" + nameToGenerate);

        AppletDescription returnedDesc = readDescription(response);
        assertEquals(returnedDesc.getName(), nameToGenerate);
    }

    // TODO: *NotFound
    @Test
    public void getInfo() throws IOException {
        AppletDescription desc = new AppletDescription("test");

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);

        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        AppletDescription returnedDesc = readDescription(response);
        assertEquals(returnedDesc.getName(), "test");
    }

    @Test
    public void getState() throws IOException {
        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getState()).thenReturn(AppletInstanceState.STARTED);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        AppletInstanceState returnedState = readState(response);
        assertEquals(returnedState, AppletInstanceState.STARTED);
    }

    // TODO: *NotFound
    // TODO: *InvalidTransition
    @Test
    public void setStateSuccess() throws IOException {
        StateEvent stateEvent = new StateEvent(AppletInstanceState.STOPPED);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getState()).thenReturn(AppletInstanceState.STOPPED);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(stateEvent, MediaType.APPLICATION_JSON_TYPE));

        verify(applet).stop();
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        AppletInstanceState returnedState = readState(response);
        assertEquals(returnedState, AppletInstanceState.STOPPED);
    }

    // TODO: *visible*

    // TODO: *callMethod*

    // TODO: *destroy*

    private AppletInstanceState readState(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.readEntity(String.class), StateResult.class).getState();
    }

    private AppletDescription readDescription(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.readEntity(String.class), AppletDescription.class);
    }
}
