## GDS Server Simulator

This example serves as very basic simulator for the GDS system.

It is shipped as a `Java (maven)`  application - its dependencies are [Netty](https://github.com/netty/netty) (`v4.1.72.Final`) and the [Java SDK](https://github.com/arh-eu/gds-java-sdk) (`v2.0.0`) for the GDS (and the [MessagePack](https://github.com/msgpack/msgpack-java) (`v0.9.0`) for the communication protocol/values). 
 
## How to build

To build and run the simulator you will need: 

* [Oracle JDK 8](http://www.oracle.com/technetwork/java/) or newer
* [Apache Maven](http://maven.apache.org/)

## How to run

The entry point of the application is in the `hu.gds.examples.simulator.Main` class.

## What can it do?

It can receive requests and send response messages specified in the [Wiki](https://github.com/arh-eu/gds/wiki/Messages). 

## How does it work?

It runs a simple WebSocketServer, and passes the contents of the incoming messages to the `GDSSimulator` class. It will interpret it as a MessagePack-ed request, unpack it by the MessagePack specifications, identify the message data type, and based on that information will reply with an appropriate response (ACK) message.

## What can I use it for?

To test, tune and develop your client application (even offline) without having access to a fully working GDS system.

## What are the limits?

Since this is just a basic simulator, it has no underlying storage, SQL-parsing or PermissionConfig integration, or any of the business logic implemented, meaning the data is _static_ and dummy in the program - you cannot effectively modify it or use it as a live GDS instance.

Also, many details of your requests will be ignored - it is not the goal of this software to cover the functionality of the GDS. 

This does not mean your requests are not checked at all - packages violating the structures of the messages will be handled like invalid requests, meaning the response will contain description of these errors. 

## How accurate is it?

 &#x26A0; The fieldset used in the `SELECT` reply can differ for each instance based on the configuration and custom settings set by the system admin/operator. These are placeholder values and types that are the most common across installed GDS systems.

The values used are of the right type, but their actual value and/or correct range might not be accurate (i.e. licence plates are random alpha-numeric strings without country-specific formats or letters).

Null values are also supplied for things that might not be `null` in a live system but will be by default if not configured otherwise.

The same way providing all available values on specific types cannot/will not be done for business reasons. 

## Which scenarios are covered then?


First if all, keep in mind that if you try to send any request without a connection message first your request will be declined by an appropriate response message with `Error 401 - Unauthorized`.

| What can you send? | What will you receive? | Comments |
| ------------------ | ---------------------- | -------- |
| [Connection](https://github.com/arh-eu/gds/wiki/Connection) | [ConnectionACK](https://github.com/arh-eu/gds/wiki/Connection-ACK) | The username in the request must be `"user"`. Trying to connect with any other username will result in an error. |
| [Event](https://github.com/arh-eu/gds/wiki/Event) | [EventACK](https://github.com/arh-eu/gds/wiki/Event-ACK) | Contents of the response are about two successful `INSERT` statements.  |
| [AttachmentRequest](https://github.com/arh-eu/gds/wiki/Attachment-request) | [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Attachment-request-ACK) | The response will contain one record with the attachment set to a white pixel in `BMP` format. | 
| [AttachmentRequestACK](https://github.com/arh-eu/gds/wiki/Attachment-request-ACK) | `<nothing>` | ACK messages are not ACK'd again because this would lead to the [Two Generals' Problem](https://en.wikipedia.org/wiki/Two_Generals%27_Problem). | 
| [AttachmentResponse](https://github.com/arh-eu/gds/wiki/Attachment-response) | [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Attachment-response-ACK) | Response will contain one record with a white pixel attached. |
| [AttachmentResponseACK](https://github.com/arh-eu/gds/wiki/Attachment-response-ACK) | `<nothing>` | _`<same as AttachmentRequestACK>`_ |
| [EventDocument](https://github.com/arh-eu/gds/wiki/EventDocument) | [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Event-Document-ACK) | Contents of the response will contain 3 records with code `202`, without notification. |
| [QueryRequest](https://github.com/arh-eu/gds/wiki/Query-request) | [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK) | Contents of your request (the specified SQL-string) will be ignored and the response will be filled with some predefined values. The response will _always_ say that this query is continuable.|
|[NextQueryPageRequest](https://github.com/arh-eu/gds/wiki/Next-Query-Page-request) |[QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK) | This will look like it is resuming an existing request from the rows it stopped last time. This request is "_not_" continuable. |


The following messages are ignored and the response will contain an `Error 400 - Bad Request` message without any data. The ACK type is the same as the request:

  - [ConnectionACK](https://github.com/arh-eu/gds/wiki/Connection-ACK)
  - [EventACK](https://github.com/arh-eu/gds/wiki/Event-ACK)
  - [EventDocumentACK](https://github.com/arh-eu/gds/wiki/Event-Document-ACK)
  - [QueryRequestACK](https://github.com/arh-eu/gds/wiki/Query-request-ACK)

## ACKs containing error message 

If you want to have the option the get messages with error codes, you can do this by adding the ` GDSSimulator.setErrorPercentage(..);` call to the `Main` class before instantiating the WebSocketServer. (Value should be between `0` and `100`, but the login ACK is not affected by this). This will make the GDS to have a chance to reply with an error to your requests. If the margin is met, instead of a successful message you'll get the followings: 

By default, this is set to `10%`.

| Message you send | Message you receive | Error code | Error message |
|---|---|---|---|
|Event | Event ACK | 304 | "This record was already inserted to the GDS."|
 |Attachment Request | Attachment Request ACK | 401 | "User has no right to access this attachment." |
 |Attachment Response | Attachment Response ACK |  410 | "This attachment will not be stored as its time to live expired."
 | Event Document | Event Document ACK | 304 |  "This record was already inserted to the GDS." |
 |Query Request | Query Request ACK | 412 | "The user has no SELECT right for the given table." |
 Next Query Page Request | Query Request ACK |  406 | "The given query cannot be continued as there are no more records." |
