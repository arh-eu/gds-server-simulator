## GDS Server Simulator

This example serves as very basic simulator for the GDS system.

It is shipped as a `Java (maven)` application - its dependency is
the [GDS Java SDK](https://github.com/arh-eu/gds-java-sdk) (`v3.0.0`) for the GDS (which transitively includes our other
two main dependencies, namely [Netty](https://github.com/netty/netty) (`v4.1.77.Final`) and
the [MessagePack](https://github.com/msgpack/msgpack-java) (`v0.9.1`) implementation).

## How to build

To build and run the simulator you will need:

* [JDK](https://jdk.java.net/) implementation supporting Java 17
* [Apache Maven](http://maven.apache.org/)

## How to run

The entry point of the application is in the `hu.gds.examples.simulator.Main` class. You can either open the project in
your IDE to run it or
you can compile it and run it via the `java -jar gds-server-simulator-3.1.0-jar-with-dependencies.jar` command from the
terminal.

## What can it do?

It can receive requests and send response messages specified in the [Wiki](https://github.com/arh-eu/gds/wiki/Messages).

## How does it work?

It runs a simple WebSocketServer, and passes the contents of the incoming messages to the `GDSSimulator` class. It will
interpret it as a MessagePack-ed request, unpack it by the MessagePack and GDS standard's specifications, identify the
message data type, and based on that information will reply with an appropriate response (ACK) message.

## What can I use it for?

To test, tune and develop your client application (even offline) without having access to a fully working GDS system.

## What are the limits?

Since this is just a basic simulator, it has no underlying storage, SQL-parsing or PermissionConfig integration, or any
of the business logic implemented, meaning the data is _static_ and dummy in the program - you cannot effectively modify
it or use it as a live GDS instance.

Also, many details of your requests will be ignored - it is not the goal of this software to cover the business
functionality of the GDS.

This does not mean your requests are not checked at all - packages violating the structures of the messages will be
handled like invalid requests, meaning the response will contain description of these errors.

## Can it send PUSH messages?

Yes. If the `serve_on_the_same_connection` flag in the login message is set to false and the username is listed under
the `send_push_messages_to` key in the `settings.properties` file then PUSH messages with 3 events will be sent
automatically every 10 seconds (by default - can be changed) to the user.

## How accurate is it?

&#x26A0; The fieldset used in the `SELECT` reply can differ for each GDS instance based on the configuration and custom
settings set by the system admin/operator. The fields sent back are placeholder values and types that are the most
common across installed GDS systems.

The values used are of the right type, but their actual value and/or correct range might not be accurate (i.e. license
plates are random alphanumeric strings without country-specific formats or letters - front and backplates can differ in
format as well).

Null values are also supplied for things that might not be `null` in a live system but will be by default if not
configured otherwise.

The same way providing all available values on specific types cannot/will not be done for business reasons.

## Which scenarios are covered then?

First if all, keep in mind that if you try to send any request without a connection message first your request will be
declined by an appropriate response message with `Error 401 - Unauthorized`.

| What can you send?                                                                                             | What will you receive?                                                                                         | Comments                                                                                                                                                                                        |
|----------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Connection](https://github.com/arh-eu/gds/wiki/Message-Data#start-connection---data-type-0)                   | [ConnectionACK](https://github.com/arh-eu/gds/wiki/Message-Data#start-connection-ack---data-type-1)            | The username in the request must be specified in the `settings.properties`. Trying to connect with any other username will result in an error, see below.                                       |
| [Event](https://github.com/arh-eu/gds/wiki/Message-Data#event---data-type-2)                                   | [EventACK](https://github.com/arh-eu/gds/wiki/Message-Data#event-ack---data-type-3)                            | Contents of the response are about two successful `INSERT` statements.                                                                                                                          |
| [AttachmentRequest](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-request---data-type-4)          | [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-request-ack---data-type-5)   | The response will contain one record with the attachment set to a white pixel in `BMP` format.                                                                                                  | 
| [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-request-ack---data-type-5)   | `<nothing>`                                                                                                    | ACK messages are not ACK'd again because this would lead to the [Two Generals' Problem](https://en.wikipedia.org/wiki/Two_Generals%27_Problem).                                                 | 
| [AttachmentResponse](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-response---data-type-6)        | [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-response-ack---data-type-7) | Response will contain one record with a `9x9` image attached. The image is either `bmp` or `png` format, randomly chosen (but a valid image).                                                   |
| [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Message-Data#attachment-response-ack---data-type-7) | `<nothing>`                                                                                                    | _`<same as AttachmentRequestACK>`_                                                                                                                                                              |
| [EventDocument](https://github.com/arh-eu/gds/wiki/Message-Data#event-document---data-type-8)                  | [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Message-Data#event-document-ack---data-type-9)           | Contents of the response will contain 3 records with code `202`, without notification.                                                                                                          |
| [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Message-Data#event-document-ack---data-type-9)           | `<nothing>`                                                                                                    | _`<same as AttachmentRequestACK>`_                                                                                                                                                              |
| [QueryRequest](https://github.com/arh-eu/gds/wiki/Message-Data#query-request---data-type-10)                   | [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Message-Data#query-request-ack---data-type-11)            | Contents of your request (the specified SQL-string) will be ignored and the response will be filled with some predefined values. The response will _always_ say that this query is continuable. |
| [NextQueryPageRequest](https://github.com/arh-eu/gds/wiki/Message-Data#next-query-page---data-type-12)         | [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Message-Data#query-request-ack---data-type-11)            | This will look like it is resuming an existing request from the rows it stopped last time. This request is "_not_" continuable.                                                                 |

The following messages are ignored and the response will contain an `Error 400 - Bad Request` message without any data.
The ACK type is the same as the request:

- [ConnectionACK](https://github.com/arh-eu/gds/wiki/Connection-ACK)
- [EventACK](https://github.com/arh-eu/gds/wiki/Event-ACK)
- [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK)

## How can I configure it?

The simulator has a `settings.properties` file attached to it. This file can be used to statically preset some values in
the system.

- The port the simulator is using with the `port` key. Integer value must be used.
- The users allowed logging in are listed under the `users` key. This is a _comma-separated_ list. The user
  named `"user"` is always present, regardless of the settings.
- The simulator can be set up to have a chance to reply with an error to your requests with the `error_percentage` key.
  Its value has to be between `0` and `100`, but keep in mind the login ACK is not affected by this, it always checks
  for the registered users.
- The users the simulator will send PUSH messages to are listed under the `send_push_messages_to` key. This is a _
  comma-separated_ list. The names must be present under the `users` key, otherwise exception will be thrown.
- The `push_message_interval` sets the time between sending PUSH messages, in milliseconds.

If the properties file is not to be found, the following values will be used: `8888 / "user" / 10%`. PUSH events will
not be sent in this case.

However, __if the properties file__ exists and can be opened but __is invalid__, the simulator will
call __`System.exit(1);`__
upon trying to load its settings before printing the exception to the standard error stream.

## Dynamic usage

If you need to, you can also include the simulator as a dependency in your own project. To dynamically set the values
mentioned above, you can do it by invoking the static methods of the `GDSSimulator` class.

- The allowed users can be modified by the `addUser(String)` and the `removeUser(String)` methods. To check for users
  you
  can use the `boolean hasUser(String)` or the `Set<String> getRegisteredUsers()` methods.
- The error percentage can be set by the `setErrorPercentage(int)` method. This will throw an `IllegalArgumentException`
  if its value is not between `0` and `100`.

If you want to modify the default port (`8888`) you can do so in the `GDSSimulator` class's `PORT` field.

To run the simulator, simply instantiate the `hu.gds.examples.simulator.websocket.WebSocketServer` class and invoke
its `run()` method, similarly how we do it in our `Main`. As the server class implements the `AutoClosable` interface to
clean up the connection properly,
you should use a `try-with-resources` block:

```java
import hu.gds.examples.simulator.websocket.WebSocketServer;

public class Main {
    public static void main(String[] args) {
        try (WebSocketServer wss = new WebSocketServer()) {
            wss.run();
        }
    }
}
```

## Simulator closing the WebSocket connection

In some cases, the simulator will send a `CloseWebSocketFrame` to your client and close the connection towards you.
As the specification states, before the client sends any requests, the connection towards the GDS should be established
with a `CONNECTION_0` message.
The GDS and so the Simulator will close the WebSocket connection if any of the followings are met:

- The user is trying to send a `Start Connection (0)` message while already having an active and successful connection (
  positive `Connection ACK (1)` received.) on the current WebSocket channel.
- The user is sending any, but a `Start Connection (0)` message without a previous successful connection.

## ACKs containing error message

By setting the error percentage to a non-zero value, if the margin is met, instead of a successful message you'll get
the following reply based on your request:

| Message you send        | Message you receive     | Error code | Error message                                                       |
|-------------------------|-------------------------|------------|---------------------------------------------------------------------|
| Event                   | Event ACK               | 304        | "This record was already inserted to the GDS."                      |
| Attachment Request      | Attachment Request ACK  | 401        | "User has no right to access this attachment."                      |
| Attachment Response     | Attachment Response ACK | 410        | "This attachment will not be stored as its time to live expired."   |
| Event Document          | Event Document ACK      | 304        | "This record was already inserted to the GDS."                      |
| Query Request           | Query Request ACK       | 412        | "The user has no SELECT right for the given table."                 |
| Next Query Page Request | Query Request ACK       | 406        | "The given query cannot be continued as there are no more records." |
