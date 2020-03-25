/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/01/29
 */

package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.*;
import hu.arh.gds.message.data.impl.AckStatus;
import hu.arh.gds.message.data.impl.EventResultHolderImpl;
import hu.arh.gds.message.data.impl.EventSubResultHolderImpl;
import hu.arh.gds.message.data.impl.FieldHolderImpl;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class EventACK {
    public static MessageData3EventAck getData() throws IOException, ValidationException {
        MessageData3EventAck responseData;
        if (user_logged_in) {
            List<EventResultHolder> eventResultHolders = new ArrayList<>();
            EventResultHolder eventResultHolder1 = new EventResultHolderImpl(
                    AckStatus.OK,
                    null,
                    new ArrayList<FieldHolder>() {{
                        add(new FieldHolderImpl("field_name_example1", FieldValueType.TEXT, ""));
                        add(new FieldHolderImpl("field_name_example2", FieldValueType.BOOLEAN, ""));
                    }},
                    new ArrayList<EventSubResultHolder>() {{
                        add(new EventSubResultHolderImpl(
                                AckStatus.OK,
                                "EVNT202001290039071890",
                                "sample_table",
                                true,
                                1L,
                                null));
                    }});
            EventResultHolder eventResultHolder2 = new EventResultHolderImpl(
                    AckStatus.OK,
                    null,
                    new ArrayList<FieldHolder>() {{
                        add(new FieldHolderImpl("field_name_example1", FieldValueType.TEXT, ""));
                        add(new FieldHolderImpl("field_name_example2", FieldValueType.BINARY, ""));
                    }},
                    new ArrayList<EventSubResultHolder>() {{
                        add(new EventSubResultHolderImpl(
                                AckStatus.OK,
                                "ATID202001290039071890",
                                "sample_table-@attachment",
                                true,
                                1L,
                                null));
                    }});
            eventResultHolders.add(eventResultHolder1);
            eventResultHolders.add(eventResultHolder2);
            responseData = MessageManager.createMessageData3EventAck(eventResultHolders, AckStatus.OK, null);
        } else {
            responseData = MessageManager.createMessageData3EventAck(
                    null,
                    AckStatus.UNAUTHORIZED,
                    "This user does not exist or has not sent Connection request yet!");
        }
        return responseData;
    }
}
