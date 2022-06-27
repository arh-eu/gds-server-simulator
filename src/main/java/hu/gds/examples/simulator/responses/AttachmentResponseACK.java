/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
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

import java.util.Arrays;
import java.util.Collections;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class AttachmentResponseACK {
    public static MessageData7AttachmentResponseAck getData() throws ValidationException {
        long ttl_toValid = 60 * 60 * 1000L;
        String mimetype;
        boolean sendBMP = false;

        if (RANDOM.nextInt() % 2 == 0) {
            mimetype = "image/bmp";
            sendBMP = true;
        } else {
            mimetype = "image/png";
        }

        return MessageManager.createMessageData7AttachmentResponseAck(
                AckStatus.OK,
                new AttachmentResponseAckResultHolderImpl(
                        AckStatus.OK,
                        new AttachmentResultHolderImpl(
                                Arrays.asList("request_id_1", "request_id_2"),
                                "sample_owner_table",
                                "attachment_id_1",
                                Collections.singletonList("owner1"),
                                mimetype,
                                ttl_toValid,
                                ttl_toValid,
                                RandomUtil.getImagePixels(sendBMP))),
                null);
    }
}
