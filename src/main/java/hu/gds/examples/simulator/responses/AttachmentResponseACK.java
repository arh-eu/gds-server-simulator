/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/02/06
 */

package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.MessageData7AttachmentResponseAck;
import hu.arh.gds.message.data.impl.AckStatus;
import hu.arh.gds.message.data.impl.AttachmentResponseAckResultHolderImpl;
import hu.arh.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;

import java.io.IOException;
import java.util.ArrayList;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class AttachmentResponseACK {
    public static MessageData7AttachmentResponseAck getData() throws IOException, ValidationException {
        MessageData7AttachmentResponseAck responseData;
        if(user_logged_in) {
            responseData = MessageManager.createMessageData7AttachmentResponseAck(
                    AckStatus.OK,
                    new AttachmentResponseAckResultHolderImpl(
                            AckStatus.OK,
                            new AttachmentResultHolderImpl(
                                    new ArrayList<String>(){{add("request_id_1");add("request_id_2");}},
                                    "sample_owner_table",
                                    "attachment_id_1",
                                    new ArrayList<String>(){{add("owner1");}},
                                    "image/bmp",
                                    60 * 60 * 1000L,
                                    60 * 60 * 1000L,
                                    AttachmentRequestACK.getImagePixels())),
                    null);
        } else {
            responseData = MessageManager.createMessageData7AttachmentResponseAck(
                    AckStatus.UNAUTHORIZED,
                    null,
                    "This user does not exist or has not sent Connection request yet!");
        }
        return responseData;
    }
}
