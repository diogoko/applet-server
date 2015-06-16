package com.github.diogoko;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

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
        try {
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
                status = Response.Status.SEE_OTHER;

            }

            AppletDescription description = applet.getDescription();
            return Response.status(status).location(buildAppletLocation(description)).entity(description).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().build();
        }
    }

    @GET
    @Path("{name}")
    public Response getInfo(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(applet.getDescription()).build();
        }
    }

    @GET
    @Path("{name}/state")
    public Response getState(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            StateResult result = new StateResult();
            result.setState(applet.getState());

            return Response.ok(result).build();
        }
    }

    @PUT
    @Path("{name}/state")
    public Response setState(@PathParam("name") String name, StateEvent event) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (event.getState().equals(AppletInstanceState.STARTED)) {
                applet.start();
            } else if (event.getState().equals(AppletInstanceState.STOPPED)) {
                applet.stop();
            } else {
                throw new IllegalArgumentException();
            }

            StateResult result = new StateResult();
            result.setState(applet.getState());

            return Response.ok(result).build();
        }
    }

    @GET
    @Path("{name}/visible")
    public Response isVisible(@PathParam("name") String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok().build();
        }
    }

    @PUT
    @Path("{name}/visible")
    public Response isVisible(@PathParam("name") String name, VisibleEvent event) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (event.isVisible()) {
                applet.show();
            } else {
                applet.hide();
            }

            return Response.ok().build();
        }
    }

    @POST
    @Path("{appletName}/methods/{methodName}")
    public Response callMethod(@PathParam("appletName") String appletName, @PathParam("methodName") String methodName, CallMethodEvent event) {
        AppletInstance applet = getAppletContainer().findByName(appletName);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            Object methodResult = applet.callMethod(methodName, event.getArgs());

            CallMethodResult result = new CallMethodResult();
            result.setResult(methodResult);

            return Response.ok(result).build();
        }
    }

    @DELETE
    @Path("{name}")
    public Response destroy(String name) {
        AppletInstance applet = getAppletContainer().findByName(name);
        if (applet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            applet.hide();
            applet.stop();
            applet.destroy();

            getAppletContainer().removeApplet(name);

            return Response.ok().build();
        }
    }

    private URI buildAppletLocation(AppletDescription description) {
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();

        return ub.path("applets/{name}").build(description.getName());
    }
}
