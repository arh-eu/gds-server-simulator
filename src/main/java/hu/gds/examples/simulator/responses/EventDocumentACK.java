/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/02/03
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.EventDocumentResultHolder;
import hu.arheu.gds.message.data.MessageData9EventDocumentAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.EventDocumentResultHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.util.MessageManager;

import java.util.ArrayList;
import java.util.HashMap;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class EventDocumentACK {
    public static MessageData9EventDocumentAck getData() throws ValidationException {
        MessageData9EventDocumentAck responseData;
        if (user_logged_in) {
            responseData = MessageManager.createMessageData9EventDocumentAck(
                    AckStatus.OK,
                    new ArrayList<EventDocumentResultHolder>() {{
                        add(new EventDocumentResultHolderImpl(
                                AckStatus.OK,
                                null,
                                new HashMap<>()));
                    }},
                    null);
        } else {
            responseData = MessageManager.createMessageData9EventDocumentAck(
                    AckStatus.UNAUTHORIZED,
                    null,
                    "This user does not exist or has not sent Connection request yet!");
        }
        return responseData;
    }
}
