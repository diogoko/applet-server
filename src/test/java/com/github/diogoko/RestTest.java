package com.github.diogoko;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.applet.AppletDescription;
import com.github.diogoko.applet.AppletInstance;
import com.github.diogoko.applet.AppletInstanceState;
import com.github.diogoko.rest.AppletsResourceConfig;
import com.github.diogoko.rest.event.CallMethodEvent;
import com.github.diogoko.rest.event.CreateEvent;
import com.github.diogoko.rest.event.StateEvent;
import com.github.diogoko.rest.event.VisibleEvent;
import com.github.diogoko.rest.result.CallMethodResult;
import com.github.diogoko.rest.result.StateResult;
import com.github.diogoko.rest.result.VisibleResult;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.InOrder;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RestTest extends JerseyTest {

    private AppletContainer container;

    @Override
    protected Application configure() {
        container = mock(AppletContainer.class);
        Options options = new Options();
        options.setAllowOrigin(new HashSet<>(Arrays.asList("*")));
        return new AppletsResourceConfig(container, options);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(FixedOriginRequestFilter.class);
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

        verify(applet).start();
        verify(applet, never()).show();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Location"));
        AppletDescription returnedDesc = readJSON(response, AppletDescription.class);
        assertEquals("test", returnedDesc.getName());
    }

    @Test
    public void createExisting() throws URISyntaxException, IOException {
        AppletDescription desc = new AppletDescription("test");
        CreateEvent startEvent = new CreateEvent(desc, false);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);

        Response response = target("applets")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(startEvent, MediaType.APPLICATION_JSON_TYPE));

        verify(applet, never()).start();
        verify(applet, never()).show();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Location"));
        AppletDescription returnedDesc = readJSON(response, AppletDescription.class);
        assertEquals("test", returnedDesc.getName());
    }

    @Test
    public void createNamedAndShow() throws URISyntaxException, IOException {
        AppletDescription desc = new AppletDescription("test");
        CreateEvent startEvent = new CreateEvent(desc, true);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(null);
        when(container.createApplet(any(AppletDescription.class))).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);

        Response response = target("applets")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(startEvent, MediaType.APPLICATION_JSON_TYPE));

        InOrder inOrder = inOrder(applet);
        inOrder.verify(applet).start();
        inOrder.verify(applet).show();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Location"));
        AppletDescription returnedDesc = readJSON(response, AppletDescription.class);
        assertEquals("test", returnedDesc.getName());
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

        verify(applet).start();
        verify(applet, never()).show();

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString("Location"));

        AppletDescription returnedDesc = readJSON(response, AppletDescription.class);
        assertEquals(nameToGenerate, returnedDesc.getName());
    }

    @Test
    public void getInfoSuccess() throws IOException {
        AppletDescription desc = new AppletDescription("test");

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getDescription()).thenReturn(desc);

        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        AppletDescription returnedDesc = readJSON(response, AppletDescription.class);
        assertEquals("test", returnedDesc.getName());
    }

    @Test
    public void getInfoNotFound() throws IOException {
        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getStateSuccess() throws IOException {
        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.getState()).thenReturn(AppletInstanceState.STARTED);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        AppletInstanceState returnedState = readJSON(response, StateResult.class).getState();
        assertEquals(AppletInstanceState.STARTED, returnedState);
    }

    @Test
    public void getStateNotFound() throws IOException {
        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

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

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        AppletInstanceState returnedState = readJSON(response, StateResult.class).getState();
        assertEquals(AppletInstanceState.STOPPED, returnedState);
    }

    @Test
    public void setStateNotFound() throws IOException {
        StateEvent stateEvent = new StateEvent(AppletInstanceState.STOPPED);

        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(stateEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void setStateInvalidTransition() throws IOException {
        StateEvent stateEvent = new StateEvent(AppletInstanceState.DESTROYED);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);

        Response response = target("applets/test/state")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(stateEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getVisibleNotFound() throws IOException {
        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test/visible")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void setVisibleTrueSuccess() throws IOException {
        VisibleEvent visibleEvent = new VisibleEvent(true);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.isVisible()).thenReturn(true);

        Response response = target("applets/test/visible")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(visibleEvent, MediaType.APPLICATION_JSON_TYPE));

        verify(applet).show();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        boolean returnedVisible = readJSON(response, VisibleResult.class).isVisible();
        assertEquals(true, returnedVisible);
    }

    @Test
    public void setVisibleFalseSuccess() throws IOException {
        VisibleEvent visibleEvent = new VisibleEvent(false);

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.isVisible()).thenReturn(false);

        Response response = target("applets/test/visible")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(visibleEvent, MediaType.APPLICATION_JSON_TYPE));

        verify(applet).hide();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        boolean returnedVisible = readJSON(response, VisibleResult.class).isVisible();
        assertEquals(false, returnedVisible);
    }

    @Test
    public void setVisibleNotFound() throws IOException {
        VisibleEvent visibleEvent = new VisibleEvent(true);

        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test/visible")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(visibleEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void callMethodSuccess() throws IOException {
        CallMethodEvent callMethodEvent = new CallMethodEvent(new Object[] { 10, 5 });

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.callMethod("sum", new Object[] { 10, 5 })).thenReturn(15);

        Response response = target("applets/test/methods/sum")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(callMethodEvent, MediaType.APPLICATION_JSON_TYPE));

        verify(applet).callMethod("sum", new Object[] { 10, 5 });

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        CallMethodResult returnedResult = readJSON(response, CallMethodResult.class);
        assertEquals(15, returnedResult.getResult());
        assertNull(returnedResult.getError());
    }

    @Test
    public void callMethodNotFound() throws IOException {
        CallMethodEvent callMethodEvent = new CallMethodEvent(new Object[] { 10, 5 });

        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test/methods/sum")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(callMethodEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void callMethodWithError() throws IOException {
        CallMethodEvent callMethodEvent = new CallMethodEvent(new Object[] { 10, 5 });

        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);
        when(applet.callMethod("sum", new Object[] { 10, 5 }))
                .thenThrow(new NullPointerException("test exception"));

        Response response = target("applets/test/methods/sum")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(callMethodEvent, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        CallMethodResult returnedResult = readJSON(response, CallMethodResult.class);
        assertNull(returnedResult.getResult());
        assertEquals("java.lang.NullPointerException", returnedResult.getError().getClassName());
        assertEquals("test exception", returnedResult.getError().getMessage());
    }

    @Test
    public void destroyNotFound() throws IOException {
        when(container.findByName("test")).thenReturn(null);

        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void destroySuccess() throws IOException {
        AppletInstance applet = mock(AppletInstance.class);
        when(container.findByName("test")).thenReturn(applet);

        Response response = target("applets/test")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        InOrder inOrder = inOrder(applet);
        inOrder.verify(applet).hide();
        inOrder.verify(applet).stop();
        inOrder.verify(applet).destroy();
        verify(container).removeApplet("test");

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private <T> T readJSON(Response response, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.readEntity(String.class), clazz);
    }

    public static class FixedOriginRequestFilter implements ClientRequestFilter {
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add("Origin", "http://test.com");
        }
    }
}
