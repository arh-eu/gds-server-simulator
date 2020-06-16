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

import java.io.IOException;
import java.nio.ByteBuffer;
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
                                    "attachemnt_id_1",
                                    new ArrayList<String>() {{
                                        add("owner1");
                                    }},
                                    "image/bmp",
                                    60 * 60 * 1000L,
                                    60 * 60 * 1000L,
                                    withAttachment ? getPixel() : null),
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

    public static byte[] getPixel() throws IOException {
        //Binary representation of a white pixel in BMP format
        int[] binaryData = {
                0x42, 0x4D, 0x3A, 0x0, 0x0, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x36, 0x0, 0x0, 0x0, 0x28, 0x0,
                0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x1, 0x0,
                0x0, 0x0, 0x1, 0x0, 0x18, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x4, 0x0, 0x0, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xFF, 0xFF,
                0xFF, 0x0
        };
        ByteBuffer byteBuffer = ByteBuffer.allocate(binaryData.length * 4);
        byteBuffer.asIntBuffer().put(binaryData);
        return byteBuffer.array();
    }
}
