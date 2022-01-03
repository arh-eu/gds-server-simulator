/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/02/03
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.MessageData5AttachmentRequestAck;
import hu.arheu.gds.message.data.impl.AckStatus;
import hu.arheu.gds.message.data.impl.AttachmentRequestAckDataHolderImpl;
import hu.arheu.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.util.MessageManager;
import hu.gds.examples.simulator.RandomUtil;

import java.io.IOException;
import java.util.ArrayList;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;
import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class AttachmentRequestACK {

    public static MessageData5AttachmentRequestAck getData(boolean withAttachment) throws IOException, ValidationException {
        MessageData5AttachmentRequestAck responseData;
        if (user_logged_in) {

            String mimetype;
            boolean sendBMP;
            if (RANDOM.nextInt() % 2 == 0) {
                mimetype = "image/bmp";
                sendBMP = true;
            } else {
                mimetype = "image/png";
                sendBMP = false;
            }

            responseData = MessageManager.createMessageData5AttachmentRequestAck(
                    AckStatus.OK,
                    new AttachmentRequestAckDataHolderImpl(
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
                                    withAttachment ? RandomUtil.getImagePixels(sendBMP) : null),
                            0L),
                    null);
        } else {
            responseData = MessageManager.createMessageData5AttachmentRequestAck(
                    AckStatus.UNAUTHORIZED,
                    null,
                    "This user does not exist or has not sent Connection request yet!");
        }
        return responseData;
    }

}
