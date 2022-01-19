/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
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
import java.util.Arrays;
import java.util.Collections;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class AttachmentRequestACK {

    public static MessageData5AttachmentRequestAck getData(boolean withAttachment) throws IOException, ValidationException {
        String mimetype = null;
        Long ttl_toValid = null;
        boolean sendBMP = false;

        if (withAttachment) {
            ttl_toValid = 60 * 60 * 1000L;
            if (RANDOM.nextInt() % 2 == 0) {
                mimetype = "image/bmp";
                sendBMP = true;
            } else {
                mimetype = "image/png";
            }
        }

        return MessageManager.createMessageData5AttachmentRequestAck
                (AckStatus.OK, new AttachmentRequestAckDataHolderImpl(
                                AckStatus.OK, new AttachmentResultHolderImpl(
                                Arrays.asList("request_id_1", "request_id_2"),
                                "sample_owner_table",
                                "attachment_id_1",
                                Collections.singletonList("owner1"),
                                mimetype,
                                ttl_toValid,
                                ttl_toValid,
                                withAttachment ? RandomUtil.getImagePixels(sendBMP) : null), 0L),
                        null);
    }

}
