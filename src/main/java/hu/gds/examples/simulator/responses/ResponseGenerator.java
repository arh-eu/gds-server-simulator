package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.*;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arheu.gds.message.data.impl.FieldHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.header.MessageHeader;
import hu.arheu.gds.message.header.MessageHeaderBase;
import hu.arheu.gds.message.util.MessageManager;
import hu.gds.examples.simulator.GDSSimulator;
import hu.gds.examples.simulator.RandomUtil;
import hu.gds.examples.simulator.fields.GDSFieldSet;
import hu.gds.examples.simulator.websocket.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.msgpack.value.Value;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class ResponseGenerator {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private static final Map<String, ScheduledFuture<?>> pushTasks = new ConcurrentHashMap<>();

    private static MessageHeaderBase getHeader(MessageHeaderBase requestHeader, MessageDataType dataType)
            throws ValidationException {
        return MessageManager.createMessageHeaderBase(
                requestHeader.getUserName(),
                requestHeader.getMessageId(),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                false,
                null,
                null,
                null,
                null,
                dataType);
    }

    public static Response getConnectionAckMessage(ChannelHandlerContext ctx, String uuid, MessageHeaderBase requestHeader, MessageData0Connection requestData)
            throws IOException, ValidationException {
        MessageData1ConnectionAck data = ConnectionACK.getData(requestHeader, requestData);

        boolean closeConnection = true;
        if (data.getGlobalStatus() == AckStatus.OK) {
            GDSSimulator.setLoggedIn(uuid, requestHeader.getUserName());
            closeConnection = false;
            if (requestData.getServeOnTheSameConnection() != null
                    && !requestData.getServeOnTheSameConnection()
                    && GDSSimulator.hasEventUser(requestHeader.getUserName())
            ) {
                pushTasks.put(uuid, executor.scheduleAtFixedRate(() -> sendPushMessage(ctx, requestHeader.getUserName()),
                        GDSSimulator.PUSH_INTERVAL_MILLIS,
                        GDSSimulator.PUSH_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS
                ));
            }
        }

        System.err.println("SENDING: " + data);
        return new Response(
                Collections.singletonList(MessageManager.createMessage(getHeader(requestHeader, MessageDataType.CONNECTION_ACK_1), data)), closeConnection);
    }

    public static Response getEventAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_ACK_3), EventACK.getData()));
    }

    public static Response getAttachmentRequestAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException {
        boolean withAttachment = RANDOM.nextBoolean();
        MessageHeader attachmentRequestAckHeader = getHeader(requestHeader, MessageDataType.ATTACHMENT_REQUEST_ACK_5);
        MessageData5AttachmentRequestAck attachmentRequestAckData = AttachmentRequestACK.getData(withAttachment);
        byte[] attachmentRequestAckMessage = MessageManager.createMessage(attachmentRequestAckHeader, attachmentRequestAckData);
        List<byte[]> binaries = new ArrayList<>();
        binaries.add(attachmentRequestAckMessage);
        if (attachmentRequestAckData.getData() != null) {
            if (attachmentRequestAckData.getData().getResult().getAttachment() == null) {
                MessageHeader attachmentResponseHeader = getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_6);

                String mimetype;
                boolean sendBMP;
                long ttl_toValid = 60 * 60 * 1000L;

                if (RANDOM.nextInt() % 2 != 0) {
                    mimetype = "image/bmp";
                    sendBMP = true;
                } else {
                    mimetype = "image/png";
                    sendBMP = false;
                }

                MessageData6AttachmentResponse attachmentResponseData = MessageManager.createMessageData6AttachmentResponse(
                        new AttachmentResultHolderImpl(
                                Arrays.asList("request_id_1", "request_id_2"),
                                "sample_owner_table",
                                "attachment_id_1",
                                Collections.singletonList("owner1"),
                                mimetype,
                                ttl_toValid,
                                ttl_toValid,
                                RandomUtil.getImagePixels(sendBMP)),
                        null);
                binaries.add(MessageManager.createMessage(attachmentResponseHeader, attachmentResponseData));
            }
        }
        return new Response(binaries);
    }

    public static Response getAttachmentResponseAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_ACK_7), AttachmentResponseACK.getData()));
    }

    public static Response getEventDocumentAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_DOCUMENT_ACK_9), EventDocumentACK.getData()));
    }

    public static Response getQueryRequestAckMessage(MessageHeaderBase requestHeader, boolean morePage)
            throws IOException, ValidationException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.QUERY_REQUEST_ACK_11), QueryACK.getData(morePage)));
    }


    public static Response getInvalidRequestMessage(MessageHeaderBase requestHeader, MessageDataType dataType)
            throws IOException, ValidationException {
        switch (dataType) {
            case EVENT_2:
                return new Response(MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_ACK_3),
                        MessageManager.createMessageData3EventAck(null, AckStatus.NOT_ACCEPTABLE_304,
                                "This record was already inserted to the GDS.")));
            case ATTACHMENT_REQUEST_4:
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_REQUEST_ACK_5),
                                MessageManager.createMessageData5AttachmentRequestAck(
                                        AckStatus.UNAUTHORIZED,
                                        null,
                                        "User has no right to access this attachment.")));
            case ATTACHMENT_RESPONSE_6:
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_ACK_7),
                                MessageManager.createMessageData7AttachmentResponseAck(
                                        AckStatus.GONE,
                                        null,
                                        "This attachment will not be stored as its time to live expired.")));
            case EVENT_DOCUMENT_8:
                return new Response(MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_DOCUMENT_ACK_9),
                        MessageManager.createMessageData9EventDocumentAck(AckStatus.NOT_ACCEPTABLE_304, null,
                                "This record was already inserted to the GDS.")));
            case QUERY_REQUEST_10:
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, MessageDataType.QUERY_REQUEST_ACK_11),
                                MessageManager.createMessageData11QueryRequestAck(
                                        AckStatus.PRECONDITION_FAILED,
                                        null,
                                        "The user has no SELECT right for the given table.")));
            case NEXT_QUERY_PAGE_12:
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, MessageDataType.QUERY_REQUEST_ACK_11),
                                MessageManager.createMessageData11QueryRequestAck(
                                        AckStatus.NOT_ACCEPTABLE_406,
                                        null,
                                        "The given query cannot be continued as there are no more records.")));
            default:
                return null;
        }
    }

    public static Response getInvalidAckMessage(MessageHeaderBase requestHeader, MessageDataType dataType)
            throws IOException, ValidationException {
        String exceptionMessage = "GDS cannot serve " + dataType.name() + " requests!";
        MessageData responseData;
        switch (dataType) {
            case EVENT_ACK_3:
                responseData = MessageManager.createMessageData3EventAck(
                        null,
                        AckStatus.BAD_REQUEST,
                        exceptionMessage);
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            case QUERY_REQUEST_ACK_11:
                responseData = MessageManager.createMessageData11QueryRequestAck(
                        AckStatus.BAD_REQUEST,
                        null,
                        exceptionMessage);
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            default:
                //unreachable
                return null;

        }
    }

    private static void sendPushMessage(ChannelHandlerContext ctx, String userName) {

        List<FieldHolder> fieldHolders = new ArrayList<>();
        List<List<Value>> values = new ArrayList<>();

        for (GDSFieldSet field : GDSFieldSet.values()) {
            fieldHolders.add(
                    new FieldHolderImpl(field.getFieldName(),
                            field.getType(),
                            field.getMimeType())
            );
        }

        for (int i = 1; i <= 3; ++i) {
            List<Value> valuesTemp = new ArrayList<>();
            for (GDSFieldSet field : GDSFieldSet.values()) {
                valuesTemp.add(field.generateValue());
            }
            values.add(valuesTemp);
        }

        try {
            byte[] binary =
                    MessageManager.createMessage(
                            MessageManager.createMessageHeaderBase(
                                    userName,
                                    MessageDataType.EVENT_DOCUMENT_8
                            ),
                            MessageManager.createMessageData8EventDocument(
                                    "sample_owner_table",
                                    fieldHolders,
                                    values
                            ));

            System.err.println("Sending automated PUSH event to user " + userName + "..");
            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));

        } catch (IOException ignored) {

        }
    }

    public static void stopEventPushing(String uuid) {
        ScheduledFuture<?> removed = pushTasks.remove(uuid);
        if (removed != null) {
            removed.cancel(false);
        }
    }
}
