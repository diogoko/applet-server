applet-server
=============

applet-server is a REST-controlled applet viewer to run applets without the Java plugin. It is installed in the client machine and listens for AJAX calls made from web pages to `localhost`. Applets can be started and stopped, and their methods can be called while they're active.

The preferred way to communicate to an applet-server instance is through [applet-server-client](https://github.com/diogoko/applet-server-client).

Status
======

Its already possible to start simple applets from [The Java Tutorials](https://docs.oracle.com/javase/tutorial/deployment/applet/), but applet-server is alpha-quality yet.

**Note that currently applet-server is extremely insecure and gives full control of your machine to any web page.**

# License

applet-server is MIT Licensed.
