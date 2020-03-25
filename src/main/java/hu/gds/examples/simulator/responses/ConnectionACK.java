/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.MessageData0Connection;
import hu.arh.gds.message.data.MessageData1ConnectionAck;
import hu.arh.gds.message.data.impl.AckStatus;
import hu.arh.gds.message.header.MessageHeaderBase;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class ConnectionACK {

    private final static String allowed_user = "user";

    public static MessageData1ConnectionAck getData(MessageHeaderBase requestHeader, MessageData0Connection requestData) throws IOException, ValidationException {
        MessageData1ConnectionAck responseData;
        if (Objects.equals(allowed_user, requestHeader.getUserName())) {
            responseData = MessageManager.createMessageData1ConnectionAck(
                    MessageManager.createMessageData0Connection(
                            false,
                            requestData.getProtocolVersionNumber(),
                            false,
                            null), null, AckStatus.OK, null);
            user_logged_in = true;
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
        return responseData;
    }
}
