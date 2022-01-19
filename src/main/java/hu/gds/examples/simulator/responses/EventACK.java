/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
 * Budapest, 2020/01/29
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.EventResultHolder;
import hu.arheu.gds.message.data.FieldValueType;
import hu.arheu.gds.message.data.MessageData3EventAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.EventResultHolderImpl;
import hu.arheu.gds.message.data.impl.EventSubResultHolderImpl;
import hu.arheu.gds.message.data.impl.FieldHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.util.MessageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventACK {
    public static MessageData3EventAck getData() throws ValidationException {

        List<EventResultHolder> eventResultHolders = new ArrayList<>();
        EventResultHolder eventResultHolder1 = new EventResultHolderImpl(
                AckStatus.OK,
                null,
                Arrays.asList(new FieldHolderImpl("field_name_example1", FieldValueType.TEXT, ""),
                        new FieldHolderImpl("field_name_example2", FieldValueType.BOOLEAN, "")),
                Collections.singletonList(new EventSubResultHolderImpl(
                        AckStatus.OK,
                        "EVNT202001290039071890",
                        "sample_table",
                        true,
                        "1",
                        null)));

        EventResultHolder eventResultHolder2 = new EventResultHolderImpl(
                AckStatus.OK,
                null,
                Arrays.asList(new FieldHolderImpl("field_name_example1", FieldValueType.TEXT, ""),
                        new FieldHolderImpl("field_name_example2", FieldValueType.BOOLEAN, "")),
                Collections.singletonList(new EventSubResultHolderImpl(
                        AckStatus.OK,
                        "ATID202001290039071890",
                        "sample_table-@attachment",
                        true,
                        "1",
                        null)));
        eventResultHolders.add(eventResultHolder1);
        eventResultHolders.add(eventResultHolder2);
        return MessageManager.createMessageData3EventAck(eventResultHolders, AckStatus.OK, null);
    }
}
