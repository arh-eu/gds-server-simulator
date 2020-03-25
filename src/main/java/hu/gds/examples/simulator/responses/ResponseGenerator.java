package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.*;
import hu.arh.gds.message.data.impl.*;
import hu.arh.gds.message.header.MessageDataType;
import hu.arh.gds.message.header.MessageHeaderBase;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;
import hu.arh.gds.message.util.WriteException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static hu.gds.examples.simulator.GDSSimulator.*;

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

    public static byte[] getConnectionAckMessage(MessageHeaderBase requestHeader, MessageData0Connection requestData)
            throws IOException, ValidationException, WriteException {
        return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.CONNECTION_ACK_1), ConnectionACK.getData(requestHeader, requestData));
    }

    public static byte[] getEventAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_ACK_3), EventACK.getData());
    }

    public static byte[] getAttachmentRequestAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_REQUEST_ACK_5), AttachmentRequestACK.getData());
    }

    public static byte[] getAttachmentResponseAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.ATTACHMENT_RESPONSE_ACK_7), AttachmentResponseACK.getData());
    }

    public static byte[] getEventDocumentAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
         return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.EVENT_DOCUMENT_ACK_9), EventDocumentACK.getData());
    }

    public static byte[] getQueryRequestAckMessage(MessageHeaderBase requestHeader)
            throws IOException, ValidationException, WriteException {
        return MessageManager.createMessage(getHeader(requestHeader, MessageDataType.QUERY_REQUEST_ACK_11), QueryACK.getData());
    }

    public static byte[] getInvalidAckMessage(MessageHeaderBase requestHeader, MessageDataType dataType)
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
                return MessageManager.createMessage(getHeader(requestHeader, dataType), responseData);
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
                return MessageManager.createMessage(getHeader(requestHeader, dataType), responseData);
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
                return MessageManager.createMessage(getHeader(requestHeader, dataType), responseData);
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
                return MessageManager.createMessage(getHeader(requestHeader, dataType), responseData);
            default:
                //unreachable
                return null;

        }
    }
}
