/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator;

import hu.arh.gds.message.data.MessageData;
import hu.arh.gds.message.header.MessageHeaderBase;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ReadException;
import hu.arh.gds.message.util.ValidationException;
import hu.arh.gds.message.util.WriteException;
import hu.gds.examples.simulator.responses.*;
import hu.gds.examples.simulator.websocket.Response;

import java.io.IOException;
import java.util.logging.Logger;

public class GDSSimulator {
    public static boolean user_logged_in = false;
    private static final Logger LOGGER = Logger.getLogger("GDSSimulator");

    public Response handleRequest(byte[] request) throws IOException, ValidationException, WriteException {

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

        LOGGER.info("GDS has received a message of type '" + requestData.getTypeHelper().getMessageDataType().name() + "'..");

        Response response;
        switch (requestData.getTypeHelper().getMessageDataType()) {
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
                response = ResponseGenerator.getInvalidAckMessage(requestHeader, requestData.getTypeHelper().getMessageDataType());
                break;
        }
        LOGGER.info("GDS Response successfully sent!");
        return response;
    }
}
