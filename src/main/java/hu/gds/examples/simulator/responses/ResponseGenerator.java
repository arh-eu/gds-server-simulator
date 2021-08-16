package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.*;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arheu.gds.message.header.MessageDataType;
import hu.arheu.gds.message.header.MessageHeader;
import hu.arheu.gds.message.header.MessageHeaderBase;
import hu.arheu.gds.message.util.MessageManager;
import hu.arheu.gds.message.util.ValidationException;
import hu.gds.examples.simulator.RandomUtil;
import hu.gds.examples.simulator.websocket.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;
import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class ResponseGenerator {

    private final static String allowed_user = "user";


    private static MessageHeaderBase getHeader(MessageHeaderBase requestHeader, MessageDataType dataType)
            throws IOException, ValidationException {
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

    public static Response getConnectionAckMessage(MessageHeaderBase requestHeader, MessageData0Connection requestData)
            throws IOException, ValidationException {
        MessageData1ConnectionAck data = ConnectionACK.getData(requestHeader, requestData);

        System.err.println("SENDING: " + data);
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.CONNECTION_ACK_1), data));
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
                if (RANDOM.nextInt() % 2 != 0) {
                    mimetype = "image/bmp";
                    sendBMP = true;
                } else {
                    mimetype = "image/png";
                    sendBMP = false;
                }

                MessageData6AttachmentResponse attachmentResponseData = MessageManager.createMessageData6AttachmentResponse(
                        new AttachmentResultHolderImpl(
                                new ArrayList<String>() {{
                                    add(requestHeader.getMessageId());
                                }},
                                "sample_owner_table",
                                "attachment_id_1",
                                new ArrayList<String>() {{
                                    add("owner1");
                                }},
                                mimetype,
                                60 * 60 * 1000L,
                                60 * 60 * 1000L,
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
            case CONNECTION_ACK_1:
                if (user_logged_in) {
                    responseData = MessageManager.createMessageData1ConnectionAck(
                            null,
                            null,
                            AckStatus.BAD_REQUEST,
                            exceptionMessage);
                } else {
                    Map<Integer, String> errors;
                    errors = new HashMap<>();
                    errors.put(0, "There is no user named '" + requestHeader.getUserName() + "'!");
                    responseData = MessageManager.createMessageData1ConnectionAck(
                            null,
                            errors,
                            AckStatus.UNAUTHORIZED,
                            "This user is not allowed!");
                }
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            case EVENT_ACK_3:
                if (user_logged_in) {
                    responseData = MessageManager.createMessageData3EventAck(
                            null,
                            AckStatus.BAD_REQUEST,
                            exceptionMessage);
                } else {
                    responseData = MessageManager.createMessageData3EventAck(
                            null,
                            AckStatus.UNAUTHORIZED,
                            "This user does not exist or has not sent Connection request yet!");
                }
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            case EVENT_DOCUMENT_ACK_9:
                if (user_logged_in) {
                    responseData = MessageManager.createMessageData9EventDocumentAck(
                            AckStatus.BAD_REQUEST,
                            null,
                            exceptionMessage);
                } else {
                    responseData = MessageManager.createMessageData9EventDocumentAck(
                            AckStatus.UNAUTHORIZED,
                            null,
                            "This user does not exist or has not sent Connection request yet!");
                }
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            case QUERY_REQUEST_ACK_11:
                if (user_logged_in) {
                    responseData = MessageManager.createMessageData11QueryRequestAck(
                            AckStatus.BAD_REQUEST,
                            null,
                            exceptionMessage);
                } else {
                    responseData = MessageManager.createMessageData11QueryRequestAck(
                            AckStatus.UNAUTHORIZED,
                            null,
                            "This user does not exist or has not sent Connection request yet!");
                }
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            default:
                //unreachable
                return null;

        }
    }
}
