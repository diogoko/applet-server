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

All requests require an `Origin` header. Requests that accept a body expect JSON-encoded data. CORS requests are answered appropriately. Fields that have a default value are optional.

## POST /applets

Start a new instance of an applet or return an existing named instance.

### Request body

The request body is an object with the following fields:

| Field  | Type    | Default | Description                                        |
|--------|---------|---------|----------------------------------------------------|
| applet | object  |         | An object describing the applet                    |
| show   | boolean | true    | Indicates if the applet will be shown when started |

The `applet` field has the format:

| Field    | Type   | Default     | Description                                                                                                                               |
|----------|--------|-------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| name     | string | (generated) | The name that will identify the applet. If no name is specified, a unique name will be generated.                                         |
| codeBase | string |             | The root URL from where `code` and `archive` will be downloaded                                                                           |
| code     | string |             | The main class of the applet. A suffix of `.class` will be ignored. Paths are relative to the `codeBase` parameter.                       |
| archive  | string | null        | The comma-separated list of JAR files that contain the applet's classes and dependencies. Paths are relative to the `codeBase` parameter. |
| width    | number | 400         | The width of the applet if it's visible                                                                                                   |
| height   | number | 300         | The height of the applet if it's visible                                                                                                  |

### Response

The response is an object with the following fields:

| Field    | Type   | Description                                                                                                                               |
|----------|--------|-------------------------------------------------------------------------------------------------------------------------------------------|
| name     | string | The name that identifies the applet                                                                                                       |
| codeBase | string | The root URL from where `code` and `archive` will be downloaded                                                                           |
| code     | string | The main class of the applet. A suffix of `.class` will be ignored. Paths are relative to the `codeBase` parameter.                       |
| archive  | string | The comma-separated list of JAR files that contain the applet's classes and dependencies. Paths are relative to the `codeBase` parameter. |
| width    | number | The width of the applet if it's visible                                                                                                   |
| height   | number | The height of the applet if it's visible                                                                                                  |

## GET /applets/{name}

Get information about an existing applet.

### Response

The response is an object with the following fields:

| Field    | Type   | Description                                                                                                                               |
|----------|--------|-------------------------------------------------------------------------------------------------------------------------------------------|
| name     | string | The name that identifies the applet                                                                                                       |
| codeBase | string | The root URL from where `code` and `archive` will be downloaded                                                                           |
| code     | string | The main class of the applet. A suffix of `.class` will be ignored. Paths are relative to the `codeBase` parameter.                       |
| archive  | string | The comma-separated list of JAR files that contain the applet's classes and dependencies. Paths are relative to the `codeBase` parameter. |
| width    | number | The width of the applet if it's visible                                                                                                   |
| height   | number | The height of the applet if it's visible                                                                                                  |

## GET /applets/{name}/state

Get the current state of an applet.

### Response

The response is an object with the following fields:

| Field | Type   | Description                                  |
|-------|--------|----------------------------------------------|
| state | string | One of INACTIVE, STARTED, STOPPED, DESTROYED |

## PUT /applets/{name}/state

Change the state of an applet. This can be used to stop or restart an applet.

### Request body

The request body is an object with the following fields:

| Field | Type   | Description                                  |
|-------|--------|----------------------------------------------|
| state | string | One of STARTED or STOPPED                    |

## GET /applets/{name}/visible

Check if an applet is currently visible.

### Response

The response is an object with the following fields:

| Field   | Type    | Description                                  |
|---------|---------|----------------------------------------------|
| visible | boolean | Indicates if the applet is currently visible |

## PUT /applets/{name}/visible

Show or hide an applet. 

### Request body

The request body is an object with the following fields:

| Field   | Type    | Description                                                                 |
|---------|---------|-----------------------------------------------------------------------------|
| visible | boolean | If `true`, the applet will be shown; if `false`, the applet will be hidden. |

### Response

The response is an object with the following fields:

| Field   | Type    | Description                                  |
|---------|---------|----------------------------------------------|
| visible | boolean | Indicates if the applet is currently visible |

## POST /applets/{appletName}/methods/{methodName}

Call a method from an started applet.

### Request body

The request body is an object with the following fields:

| Field | Type  | Description                  |
|-------|-------|------------------------------|
| args  | array | The parameters of the method |

### Response

The response is an object with the following fields:

| Field  | Type   | Description                                                                               |
|--------|--------|-------------------------------------------------------------------------------------------|
| result | object | The result of the method. Void methods return `null`.                                     |
| error  | object | Information about the exception raised by the method or `null` if no exception was raised |

The `error` field, if present, is an object with the fields:

| Field     | Type   | Description                                  |
|-----------|--------|----------------------------------------------|
| className | string | The class of the raised Exception            |
| message   | string | The message property of the raised Exception |

## DELETE /applets/{name}

Destroy an applet.

# License

applet-server is MIT Licensed.
