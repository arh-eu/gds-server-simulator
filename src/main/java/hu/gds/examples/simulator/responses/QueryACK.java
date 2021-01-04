/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator.responses;

import hu.arheu.gds.message.data.ConsistencyType;
import hu.arheu.gds.message.data.FieldHolder;
import hu.arheu.gds.message.data.FieldValueType;
import hu.arheu.gds.message.data.MessageData11QueryRequestAck;
import hu.arheu.gds.message.data.impl.*;
import hu.arheu.gds.message.util.MessageManager;
import hu.arheu.gds.message.util.ValidationException;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableStringValueImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static hu.gds.examples.simulator.GDSSimulator.user_logged_in;

public class QueryACK {

    public static MessageData11QueryRequestAck getData(boolean hasMorePage) throws IOException, ValidationException {
        MessageData11QueryRequestAck responseData;
        if (user_logged_in) {
            Random r = new Random();
            List<FieldHolder> fieldHolders = new ArrayList<>();
            for (int i = 1; i <= 3; ++i) {
                fieldHolders.add(
                        new FieldHolderImpl(
                                "field_name_" + i,
                                FieldValueType.valueOf(r.nextInt(15)),
                                "mime_type_" + i));
            }

            List<List<Value>> values = new ArrayList<>();
            for (int i = 1; i <= 100; ++i) {
                List<Value> valuesTemp = new ArrayList<>();
                for (int j = 1; j <= 3; ++j) {
                    valuesTemp.add(new ImmutableStringValueImpl("ROW_" + (hasMorePage ? i : i + 100) + "_value_" + j));
                }
                values.add(valuesTemp);
            }

            responseData = MessageManager.createMessageData11QueryRequestAck(
                    AckStatus.OK,
                    new QueryResponseHolderImpl(
                            100L,
                            10L,
                            hasMorePage,
                            new QueryContextHolderImpl(
                                    "2b22a5a84966df20a3a44793476a55c45bc06418d964bc1d9009a6e859a1bf4e",
                                    "SELECT * FROM table",
                                    0L,
                                    System.currentTimeMillis(),
                                    ConsistencyType.NONE,
                                    "BUCKET_ID",
                                    new GDSHolderImpl(
                                            "GDS_CLUSTER",
                                            "GDS_NODE"
                                    ),
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            ),
                            fieldHolders,
                            values),
                    null);
        } else {
            responseData = MessageManager.createMessageData11QueryRequestAck(
                    AckStatus.UNAUTHORIZED,
                    null,
                    "This user does not exist or has not sent Connection request yet!");
        }
        return responseData;
    }
}
