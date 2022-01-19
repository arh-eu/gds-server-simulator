/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
 * Budapest, 2020/02/03
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.MessageData9EventDocumentAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.EventDocumentResultHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.util.MessageManager;

import java.util.Collections;

public class EventDocumentACK {
    public static MessageData9EventDocumentAck getData() throws ValidationException {
        return MessageManager.createMessageData9EventDocumentAck(
                AckStatus.OK,
                Collections.singletonList(new EventDocumentResultHolderImpl(
                        AckStatus.OK,
                        null,
                        Collections.emptyMap())),
                null);
    }
}
