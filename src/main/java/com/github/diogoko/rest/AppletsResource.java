package com.github.diogoko.rest;

import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.applet.AppletDescription;
import com.github.diogoko.applet.AppletInstance;
import com.github.diogoko.applet.AppletInstanceState;
import com.github.diogoko.rest.event.CallMethodEvent;
import com.github.diogoko.rest.event.CreateEvent;
import com.github.diogoko.rest.event.StateEvent;
import com.github.diogoko.rest.event.VisibleEvent;
import com.github.diogoko.rest.result.CallMethodResult;
import com.github.diogoko.rest.result.ErrorResult;
import com.github.diogoko.rest.result.StateResult;
import com.github.diogoko.rest.result.VisibleResult;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("applets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppletsResource {
    @Context
    private Application application;

    @Context
    UriInfo uriInfo;

    private AppletContainer getAppletContainer() {
        return (AppletContainer) application.getProperties().get("appletContainer");
    }

    @POST
    public Response create(CreateEvent event) {
        AppletInstance applet = getAppletContainer().findByName(event.getApplet().getName());
        Response.Status status;
        if (applet == null) {
            applet = getAppletContainer().createApplet(event.getApplet());
            applet.start();
            if (event.isShow()) {
                applet.show();
            }

            status = Response.Status.CREATED;
        } else {
            status = Response.Status.OK;
        }

        AppletDescription description = applet.getDescription();
        return Response.status(status).entity(description).build();
    }

    @GET
    @Path("{name}")
    public Response getInfo(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            return Response.ok(applet.getDescription()).build();
        }
    }

    @GET
    @Path("{name}/state")
    public Response getState(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            StateResult result = new StateResult(applet.getState());
            return Response.ok(result).build();
        }
    }

    @PUT
    @Path("{name}/state")
    public Response setState(@PathParam("name") String name, StateEvent event) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            if (event.getState().equals(AppletInstanceState.STARTED)) {
                applet.start();
            } else if (event.getState().equals(AppletInstanceState.STOPPED)) {
                applet.stop();
            } else {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            StateResult result = new StateResult(applet.getState());
            return Response.ok(result).build();
        }
    }

    @GET
    @Path("{name}/visible")
    public Response isVisible(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            VisibleResult result = new VisibleResult(applet.isVisible());
            return Response.ok(result).build();
        }
    }

    @PUT
    @Path("{name}/visible")
    public Response setVisible(@PathParam("name") String name, VisibleEvent event) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            if (event.isVisible()) {
                applet.show();
            } else {
                applet.hide();
            }

            VisibleResult result = new VisibleResult(applet.isVisible());
            return Response.ok(result).build();
        }
    }

    @POST
    @Path("{appletName}/methods/{methodName}")
    public Response callMethod(@PathParam("appletName") String appletName, @PathParam("methodName") String methodName, CallMethodEvent event) {
        AppletInstance applet = getAppletContainer().findByName(appletName);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            CallMethodResult result = new CallMethodResult();

            try {
                Object methodResult = applet.callMethod(methodName, event.getArgs());
                result.setResult(methodResult);
            } catch (Exception e) {
                result.setError(new ErrorResult(e.getClass().getName(), e.getMessage()));
            }

            return Response.ok(result).build();
        }
    }

    @DELETE
    @Path("{name}")
    public Response destroy(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else {
            applet.hide();
            applet.stop();
            applet.destroy();

            getAppletContainer().removeApplet(name);

            return Response.noContent().build();
        }
    }
}
