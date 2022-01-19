## GDS Server Simulator

This example serves as very basic simulator for the GDS system.

It is shipped as a `Java (maven)` application - its dependency is the [GDS Java SDK](https://github.com/arh-eu/gds-java-sdk) (`v2.2.0`) for the GDS (which transitively includes our other two main dependencies, namely [Netty](https://github.com/netty/netty) (`v4.1.73.Final`) and the [MessagePack](https://github.com/msgpack/msgpack-java) (`v0.9.0`) implementation). 
 
## How to build

To build and run the simulator you will need: 

* [JDK](https://jdk.java.net/) implementation supporting at least Java 8
* [Apache Maven](http://maven.apache.org/)

## How to run

The entry point of the application is in the `hu.gds.examples.simulator.Main` class. You can either open the project in your IDE to run it or 
you can compile it and run it via the `java -jar gds-server-simulator-2.1.0-jar-with-dependencies.jar` command from the terminal.

## What can it do?

It can receive requests and send response messages specified in the [Wiki](https://github.com/arh-eu/gds/wiki/Messages). 

## How does it work?

It runs a simple WebSocketServer, and passes the contents of the incoming messages to the `GDSSimulator` class. It will interpret it as a MessagePack-ed request, unpack it by the MessagePack and GDS standard's specifications, identify the message data type, and based on that information will reply with an appropriate response (ACK) message.

## What can I use it for?

To test, tune and develop your client application (even offline) without having access to a fully working GDS system.

## What are the limits?

Since this is just a basic simulator, it has no underlying storage, SQL-parsing or PermissionConfig integration, or any of the business logic implemented, meaning the data is _static_ and dummy in the program - you cannot effectively modify it or use it as a live GDS instance.

Also, many details of your requests will be ignored - it is not the goal of this software to cover the business functionality of the GDS. 

This does not mean your requests are not checked at all - packages violating the structures of the messages will be handled like invalid requests, meaning the response will contain description of these errors. 

## How accurate is it?

 &#x26A0; The fieldset used in the `SELECT` reply can differ for each GDS instance based on the configuration and custom settings set by the system admin/operator. The fields sent back are placeholder values and types that are the most common across installed GDS systems.

The values used are of the right type, but their actual value and/or correct range might not be accurate (i.e. license plates are random alphanumeric strings without country-specific formats or letters - front and backplates can differ in format as well).

Null values are also supplied for things that might not be `null` in a live system but will be by default if not configured otherwise.

The same way providing all available values on specific types cannot/will not be done for business reasons. 

## Which scenarios are covered then?


First if all, keep in mind that if you try to send any request without a connection message first your request will be declined by an appropriate response message with `Error 401 - Unauthorized`.

| What can you send?                                                                  | What will you receive?                                                              | Comments                                                                                                                                                                                        |
|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Connection](https://github.com/arh-eu/gds/wiki/Connection)                         | [ConnectionACK](https://github.com/arh-eu/gds/wiki/Connection-ACK)                  | The username in the request must be specified in the `settings.properties`. Trying to connect with any other username will result in an error, see below.                                       |
| [Event](https://github.com/arh-eu/gds/wiki/Event)                                   | [EventACK](https://github.com/arh-eu/gds/wiki/Event-ACK)                            | Contents of the response are about two successful `INSERT` statements.                                                                                                                          |
| [AttachmentRequest](https://github.com/arh-eu/gds/wiki/Attachment-request)          | [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Attachment-request-ACK)   | The response will contain one record with the attachment set to a white pixel in `BMP` format.                                                                                                  | 
| [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Attachment-request-ACK)   | `<nothing>`                                                                         | ACK messages are not ACK'd again because this would lead to the [Two Generals' Problem](https://en.wikipedia.org/wiki/Two_Generals%27_Problem).                                                 | 
| [AttachmentResponse](https://github.com/arh-eu/gds/wiki/Attachment-response)        | [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Attachment-response-ACK) | Response will contain one record with a `9x9` image attached. The image is either `bmp` or `png` format, randomly chosen (but a valid image).                                                   |
| [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Attachment-response-ACK) | `<nothing>`                                                                         | _`<same as AttachmentRequestACK>`_                                                                                                                                                              |
| [EventDocument](https://github.com/arh-eu/gds/wiki/EventDocument)                   | [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Event-Document-ACK)           | Contents of the response will contain 3 records with code `202`, without notification.                                                                                                          |
| [QueryRequest](https://github.com/arh-eu/gds/wiki/Query-request)                    | [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK)             | Contents of your request (the specified SQL-string) will be ignored and the response will be filled with some predefined values. The response will _always_ say that this query is continuable. |
| [NextQueryPageRequest](https://github.com/arh-eu/gds/wiki/Next-Query-Page-request)  | [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK)             | This will look like it is resuming an existing request from the rows it stopped last time. This request is "_not_" continuable.                                                                 |


The following messages are ignored and the response will contain an `Error 400 - Bad Request` message without any data. The ACK type is the same as the request:

  - [ConnectionACK](https://github.com/arh-eu/gds/wiki/Connection-ACK)
  - [EventACK](https://github.com/arh-eu/gds/wiki/Event-ACK)
  - [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Event-Document-ACK)
  - [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK)

## How can I configure it?

The simulator has a `settings.properties` file attached to it. This file can be used to statically preset some values in the system.

 - The users allowed logging in are listed under the `users` key. This is a _comma-separated_ list. The user named `"user"` is always present, regardless of the settings.
 - The simulator can be set up to have a chance to reply with an error to your requests with the `error_percentage` key.
   Its value has to be between `0` and `100`, but keep in mind the login ACK is not affected by this, it always checks for the registered users.


If the properties file is not to be found, the two default values will be used (`"user" / 10%`).

However, __if the properties file__ exists and can be opened but __is invalid__, the simulator will call __`System.exit(1);`__
upon trying to load its settings before printing the exception to the standard error stream.

## Dynamic usage

If you need to, you can also include the simulator as a dependency in your own project. To dynamically set the values mentioned above, you can do it by invoking the static methods of the `GDSSimulator` class.

 - The allowed users can be modified by the `addUser(String)` and the `removeUser(String)` methods. To check for users you
   can use the `boolean hasUser(String)` or the `Set<String> getRegisteredUsers()` methods.
 - The error percentage can be set by the `setErrorPercentage(int)` method. This will throw an `IllegalArgumentException` if its value is not between `0` and `100`.

If you want to modify the default ports (`8443 / 8888`) you can do so in the `WebSocketServer` class's `PORT` field. 

To run the simulator, simply instantiate the `hu.gds.examples.simulator.websocket.WebSocketServer` class and invoke its `run()` method, similarly how we do it in our `Main`. As the server class implements the `AutoClosable` interface to clean up the connection properly,
you should use a `try-with-resources` block:

```java
import hu.gds.examples.simulator.websocket.WebSocketServer;

public class Main {
    public static void main(String[] args) {
        try(WebSocketServer wss = new WebSocketServer()){
            wss.run();
        }
    }
}
```

## Simulator closing the WebSocket connection

In some cases, the simulator will send a `CloseWebSocketFrame` to your client and close the connection towards you.
As the specification states, before the client sends any requests, the connection towards the GDS should be established with a `CONNECTION_0` message.
The GDS and so the Simulator will close the WebSocket connection if any of the followings are met:

- The user is trying to send a `Start Connection (0)` message while already having an active and successful connection (positive `Connection ACK (1)` received.) on the current WebSocket channel.
- The user is sending any, but a `Start Connection (0)` message without a previous successful connection.

## ACKs containing error message 

By setting the error percentage to a non-zero value, if the margin is met, instead of a successful message you'll get the following reply based on your request: 

| Message you send        | Message you receive     | Error code | Error message                                                       |
|-------------------------|-------------------------|------------|---------------------------------------------------------------------|
| Event                   | Event ACK               | 304        | "This record was already inserted to the GDS."                      |
 | Attachment Request      | Attachment Request ACK  | 401        | "User has no right to access this attachment."                      |
 | Attachment Response     | Attachment Response ACK | 410        | "This attachment will not be stored as its time to live expired."   |
| Event Document          | Event Document ACK      | 304        | "This record was already inserted to the GDS."                      |
 | Query Request           | Query Request ACK       | 412        | "The user has no SELECT right for the given table."                 |
| Next Query Page Request | Query Request ACK       | 406        | "The given query cannot be continued as there are no more records." |
