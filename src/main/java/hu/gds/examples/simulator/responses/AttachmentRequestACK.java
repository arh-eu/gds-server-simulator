/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/02/03
 */

package hu.gds.examples.simulator.responses;

import hu.arh.gds.message.data.MessageData5AttachmentRequestAck;
import hu.arh.gds.message.data.impl.AckStatus;
import hu.arh.gds.message.data.impl.AttachmentRequestAckDataHolderImpl;
import hu.arh.gds.message.data.impl.AttachmentResultHolderImpl;
import hu.arh.gds.message.util.MessageManager;
import hu.arh.gds.message.util.ValidationException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class AttachmentRequestACK {

    public static MessageData5AttachmentRequestAck getData(boolean withAttachment) throws IOException, ValidationException {
        MessageData5AttachmentRequestAck responseData;
        if (user_logged_in) {
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
                                    "image/bmp",
                                    60 * 60 * 1000L,
                                    60 * 60 * 1000L,
                                    withAttachment ? getImagePixels() : null),
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

    public static byte[] getImagePixels() throws IOException {
        int[] binaryData = {
                // Offset 0x00000000 to 0x00000305
                0x42, 0x4d, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00,
                0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x09, 0x00,
                0x00, 0x00, 0x01, 0x00, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0xfc, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0x24, 0x1c, 0xed, 0x24, 0x1c, 0xed, 0x24, 0x1c,
                0xed, 0x24, 0x1c, 0xed, 0x24, 0x1c, 0xed, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0x00, 0xff, 0xff, 0xff, 0x24, 0x1c, 0xed, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x24,
                0x1c, 0xed, 0xff, 0xff, 0xff, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x24, 0x1c, 0xed, 0x24,
                0x1c, 0xed, 0xff, 0xff, 0xff, 0x24, 0x1c, 0xed, 0x24, 0x1c, 0xed, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0x24, 0x1c, 0xed, 0x24, 0x1c, 0xed, 0xff, 0xff, 0xff, 0x24, 0x1c, 0xed,
                0x24, 0x1c, 0xed, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
                0xff, 0xff, 0xff, 0xff, 0xff, 0x00
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (int pixel : binaryData) {
            dos.writeByte(pixel);
        }
        return baos.toByteArray();
    }
}
