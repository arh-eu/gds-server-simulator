/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/02/06
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.MessageData7AttachmentResponseAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.AttachmentResponseAckResultHolderImpl;
import hu.arheu.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.util.MessageManager;
import hu.gds.examples.simulator.RandomUtil;

import java.io.IOException;
import java.util.ArrayList;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;
import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class AttachmentResponseACK {
    public static MessageData7AttachmentResponseAck getData() throws IOException, ValidationException {
        MessageData7AttachmentResponseAck responseData;
        if (user_logged_in) {
            String mimetype;
            boolean sendBMP;
            if (RANDOM.nextInt() % 2 == 1) {
                mimetype = "image/bmp";
                sendBMP = true;
            } else {
                mimetype = "image/png";
                sendBMP = false;
            }
            responseData = MessageManager.createMessageData7AttachmentResponseAck(
                    AckStatus.OK,
                    new AttachmentResponseAckResultHolderImpl(
                            AckStatus.OK,
                            new AttachmentResultHolderImpl(
                                    new ArrayList<String>() {{
                                        add("request_id_1");
                                        add("request_id_2");
                                    }},
                                    "sample_owner_table",
                                    "attachment_id_1",
                                    new ArrayList<String>() {{
                                        add("owner1");
                                    }},
                                    mimetype,
                                    60 * 60 * 1000L,
                                    60 * 60 * 1000L,
                                    RandomUtil.getImagePixels(sendBMP))),
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
