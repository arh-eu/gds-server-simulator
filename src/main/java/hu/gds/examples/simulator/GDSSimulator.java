/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator;

import hu.arheu.gds.message.data.MessageData;
import hu.arheu.gds.message.header.MessageDataType;
import hu.arheu.gds.message.header.MessageHeaderBase;
import hu.arheu.gds.message.util.MessageManager;
import hu.arheu.gds.message.util.ReadException;
import hu.arheu.gds.message.util.ValidationException;
import hu.gds.examples.simulator.responses.ResponseGenerator;
import hu.gds.examples.simulator.websocket.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;


public class GDSSimulator {
    public static boolean user_logged_in = false;
    private static final Logger LOGGER = Logger.getLogger("GDSSimulator");

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private String format = "[%1$tF %1$tT] [%2$s] | %3$s::%4$s | %5$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getSourceClassName(),
                        lr.getSourceMethodName(),
                        lr.getMessage()
                );
            }
        });

        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);
    }

    private static int errorPercentage = 0;

    private static final List<MessageDataType> allowFailuresFor = Arrays.asList(
            MessageDataType.EVENT_2,
            MessageDataType.ATTACHMENT_REQUEST_4,
            MessageDataType.ATTACHMENT_RESPONSE_6,
            MessageDataType.EVENT_DOCUMENT_8,
            MessageDataType.QUERY_REQUEST_10,
            MessageDataType.NEXT_QUERY_PAGE_12
    );

    public static void setErrorPercentage(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("The error percentage should be in the [0..100] range! Specified: " + value);
        }
        errorPercentage = value;
        if (errorPercentage > 0) {
            LOGGER.info("The GDS Simulator will have " + errorPercentage + "% chance to reply to your requests with an error message.");
        } else {
            LOGGER.info("The GDS Simulator will always reply to your requests with success.");
        }
    }

    public static Response handleRequest(byte[] request) throws IOException, ValidationException {

        MessageHeaderBase requestHeader;
        try {
            requestHeader = MessageManager.getMessageHeaderFromBinaryMessage(request).getTypeHelper().asBaseMessageHeader();
        } catch (ReadException | ValidationException e) {
            LOGGER.warning("An error occurred while processing the message header");
            throw new IllegalStateException(e.getMessage());
        }

        MessageData requestData;
        try {
            requestData = MessageManager.getMessageData(request);
        } catch (ReadException | ValidationException e) {
            LOGGER.warning("An error occurred while processing the message data");
            throw new IllegalStateException(e.getMessage());
        }

        MessageDataType messageDataType = requestData.getTypeHelper().getMessageDataType();
        LOGGER.info("GDS has received a message of type '" + messageDataType.name() + "'..");

        if (allowFailuresFor.contains(messageDataType) && RANDOM.nextInt(100) < errorPercentage) {
            LOGGER.info("Creating an automated response for an invalid request");
            return ResponseGenerator.getInvalidRequestMessage(requestHeader, messageDataType);
        }

        Response response;
        switch (messageDataType) {
            case CONNECTION_0:
                response = ResponseGenerator.getConnectionAckMessage(requestHeader, requestData.getTypeHelper().asConnectionMessageData0());
                LOGGER.info("Sending back the CONNECTION_ACK..");
                break;
            case EVENT_2:
                response = ResponseGenerator.getEventAckMessage(requestHeader);
                LOGGER.info("Sending back the EVENT_ACK..");
                break;
            case ATTACHMENT_REQUEST_4:
                response = ResponseGenerator.getAttachmentRequestAckMessage(requestHeader);
                LOGGER.info("Sending back the ATTACHMENT_REQUEST_ACK..");
                break;
            case ATTACHMENT_RESPONSE_6:
                response = ResponseGenerator.getAttachmentResponseAckMessage(requestHeader);
                LOGGER.info("Sending back the ATTACHMENT_RESPONSE_ACK..");
                break;
            case EVENT_DOCUMENT_8:
                response = ResponseGenerator.getEventDocumentAckMessage(requestHeader);
                LOGGER.info("Sending back the EVENT_DOCUMENT_ACK..");
                break;
            case QUERY_REQUEST_10:
                response = ResponseGenerator.getQueryRequestAckMessage(requestHeader, true);
                LOGGER.info("Sending back the QUERY_REQUEST_ACK..");
                break;
            case NEXT_QUERY_PAGE_12:
                response = ResponseGenerator.getQueryRequestAckMessage(requestHeader, false);
                LOGGER.info("Sending back the QUERY_REQUEST_ACK..");
                break;
            case ATTACHMENT_REQUEST_ACK_5:
            case ATTACHMENT_RESPONSE_ACK_7:
                return null;
            default:
                response = ResponseGenerator.getInvalidAckMessage(requestHeader, messageDataType);
                break;
        }
        LOGGER.info("GDS Response successfully sent!");
        return response;
    }
}
