# Connecting to a server

While the CDK does handle the connection to the server, connecting is a bit more of effort.

The CDK object does not contain any net-code itself.
Each connection is handled by a ``CDKConnection`` instance.
You can acquire one by calling the ``#connect`` method on the CDK instance.

When a ``CDKConnection`` tries to establish the connection it tries
to use a SSL secured connection first.
If this connection fails, it tries to fall back to an unsecured connection.

The Following ``ConnectionEvent.Interrupt`` events signal that the ``CDKConnection`` is about to try an unsecured connection:
* ``SSL_UNAVAILABLE``
* ``REJECTING_X509``
If you want to deny using a plaintext connection, use [``InterruptEvent#setCancelled(true)``](https://github.com/MarkL4YG/Homework_Server_CDK/blob/bleeding/src/main/java/de/mlessmann/homework/api/event/ICDKConnectionEvent)
In this case a ``ConnectionEvent.Close`` is fired.
(With the reason either being: ``CONNECT_FAILED`` or  ``REJECTED_X509``)

If SSL or plaintext succeeds, the connection automatically registers the greeting-listener.
When the greeting has been received, the connection is considered established and a ``ConnectionEvent`` with Status ``CONNECTED`` is fired.
(After which you can start using the ``ICDKConnection`` interface on the connection instance)
[WARNING: There's no warranty on stability if you start before the status changed to CONNECTED!]