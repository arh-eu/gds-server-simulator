package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.*;
import hu.arh.gds.message.data.impl.*;
import hu.arh.gds.message.header.MessageDataType;
import hu.arh.gds.message.header.MessageHeader;
import hu.arh.gds.message.header.MessageHeaderBase;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;
import hu.arh.gds.message.util.WriteException;
import hu.gds.examples.simulator.websocket.Response;

import java.io.IOException;
import java.util.*;

import static hu.gds.examples.simulator.GDSSimulator.*;

public class ResponseGenerator {

    private final static String allowed_user = "user";

    private static final Random random = new Random();

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
            throws IOException, ValidationException, WriteException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.CONNECTION_ACK_1), ConnectionACK.getData(requestHeader, requestData)));
    }

    public static Response getEventAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_ACK_3), EventACK.getData()));
    }

    public static Response getAttachmentRequestAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        boolean withAttachment = random.nextBoolean();
        MessageHeader attachmentRequestAckHeader = getHeader(requestHeader, MessageDataType.ATTACHMENT_REQUEST_ACK_5);
        MessageData5AttachmentRequestAck attachmentRequestAckData = AttachmentRequestACK.getData(withAttachment);
        byte[] attachmentRequestAckMessage = MessageManager.createMessage(attachmentRequestAckHeader, attachmentRequestAckData);
        List<byte[]> binaries = new ArrayList<>();
        binaries.add(attachmentRequestAckMessage);
        if(attachmentRequestAckData.getData() != null) {
            if(attachmentRequestAckData.getData().getResult().getAttachment() == null) {
                MessageHeader attachmentResponseHeader = getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_6);
                MessageData6AttachmentResponse attachmentResponseData = MessageManager.createMessageData6AttachmentResponse(
                        new AttachmentResultHolderImpl(
                                new ArrayList<String>(){{
                                    add(requestHeader.getMessageId());
                                }},
                                "sample_owner_table",
                                "attachment_id_1",
                                new ArrayList<String>() {{
                                    add("owner1");
                                }},
                                "image/bmp",
                                60 * 60 * 1000L,
                                60 * 60 * 1000L,
                                AttachmentRequestACK.getImagePixels()),
                        null);
                binaries.add(MessageManager.createMessage(attachmentResponseHeader, attachmentResponseData));
            }
        }
        return new Response(binaries);
    }

    public static Response getAttachmentResponseAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_ACK_7), AttachmentResponseACK.getData()));
    }

    public static Response getEventDocumentAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
         return new Response(
                 MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_DOCUMENT_ACK_9), EventDocumentACK.getData()));
    }

    public static Response getQueryRequestAckMessage(MessageHeaderBase requestHeader, boolean morePage)
            throws IOException, ValidationException, WriteException {
        return new Response(
                MessageManager.createMessage(getHeader(requestHeader, MessageDataType.QUERY_REQUEST_ACK_11), QueryACK.getData(morePage)));
    }

    public static Response getInvalidAckMessage(MessageHeaderBase requestHeader, MessageDataType dataType)
            throws IOException, ValidationException, WriteException {
        String exceptionMessage = "GDS cannot serve " + dataType.name() + " requests!";
        MessageData responseData;
        switch (dataType) {
            case CONNECTION_ACK_1:
                if(user_logged_in) {
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
                if(user_logged_in) {
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
                if(user_logged_in) {
                    responseData = MessageManager.createMessageMessageData9EventDocumentAck(
                            AckStatus.BAD_REQUEST,
                            null,
                            exceptionMessage);
                } else {
                    responseData = MessageManager.createMessageMessageData9EventDocumentAck(
                            AckStatus.UNAUTHORIZED,
                            null,
                            "This user does not exist or has not sent Connection request yet!");
                }
                return new Response(
                        MessageManager.createMessage(getHeader(requestHeader, dataType), responseData));
            case QUERY_REQUEST_ACK_11:
                if(user_logged_in) {
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
