/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.MessageData0Connection;
import hu.arheu.gds.message.data.MessageData1ConnectionAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.header.MessageHeaderBase;
import hu.arheu.gds.message.util.MessageManager;
import hu.gds.examples.simulator.GDSSimulator;

import java.util.HashMap;
import java.util.Map;

public class ConnectionACK {

    public static MessageData1ConnectionAck getData(MessageHeaderBase requestHeader, MessageData0Connection requestData) throws ValidationException {
        MessageData1ConnectionAck responseData;
        if (GDSSimulator.hasUser(requestHeader.getUserName())) {
            responseData = MessageManager.createMessageData1ConnectionAck(
                    MessageManager.createMessageData0Connection(
                            false,
                            requestData.getProtocolVersionNumber(),
                            false,
                            null), null, AckStatus.OK, null);
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
