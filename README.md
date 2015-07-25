# applet-server

applet-server is a REST-controlled applet viewer to run applets without the Java plugin. It is installed in the client machine and listens for AJAX calls made from web pages to `localhost`. Applets can be started and stopped, and their methods can be called while they're active.

The preferred way to communicate to an applet-server instance is through [applet-server-client](https://github.com/diogoko/applet-server-client).

# Status

Its already possible to start simple applets from [The Java Tutorials](https://docs.oracle.com/javase/tutorial/deployment/applet/), but applet-server is alpha-quality yet.

**Note that currently applet-server is extremely insecure and gives full control of your machine to any web page.**

# Usage

Configure the options of the server in `server.properties` and start the `com.github.diogoko.AppletServer` class. The options file must be in the working directory.

# Options

## port

The port where the server will listen to wait for the requests.

<dl>
    <dt>Required</dt>
    <dd>Yes</dd>
    
    <dt>Default</dt>
    <dd><code>9998</code></dd>
    
    <dt>Example</dt>
    <dd><code>port = 9998</code></dd>
</dl>

## allowOrigin

A comma-separated list of the URLs that are allowed in the `Origin` header. All requests must have the `Origin` header to be accepted.

Using `*` means that any origin is allowed.

<dl>
    <dt>Required</dt>
    <dd>Yes</dd>
    
    <dt>Default</dt>
    <dd><code>*</code></dd>
    
    <dt>Example</dt>
    <dd><code>allowOrigin = ht<span>tp:</span>//www.example.com/applets/calculator.html</code></dd>
</dl>

# API

## POST /applets

## GET /applets/{name}

## GET /applets/{name}/state

## PUT /applets/{name}/state

## GET /applets/{name}/visible

## PUT /applets/{name}/visible

## POST /applets/{appletName}/methods/{methodName}

## DELETE /applets/{name}

# License

applet-server is MIT Licensed.
