package hu.gds.examples.simulator.fields;

import hu.arheu.gds.message.data.FieldValueType;
import org.msgpack.value.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static hu.gds.examples.simulator.RandomUtil.*;

public enum GDSFieldSet {
    //system fields
    SYSTEM_TIMESTAMP("@timestamp", "\"type\":\"datetime\"", FieldValueType.LONG, () -> {
        return FieldValueType.LONG.valueFromObject(System.currentTimeMillis() - 3_600_000); //one hour before now
    }),
    SYSTEM_TO_VALID("@to_valid", "\"type\":\"datetime\"", FieldValueType.LONG, () -> FieldValueType.LONG.valueFromObject(randomLong(System.currentTimeMillis(), Long.MAX_VALUE))),
    SYSTEM_TTL("@ttl", "", FieldValueType.LONG, () -> FieldValueType.LONG.valueFromObject(randomLong(System.currentTimeMillis(), Long.MAX_VALUE))),
    SYSTEM_VERSION("@@version", "", FieldValueType.KEYWORD, () -> FieldValueType.LONG.valueFromObject(randomLong(100L))),
    ACTION_EVAL_STATE("action_eval_state", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("FIN")),
    ACTION_EVAL_SUB_STATES("action_eval_sub_states", "text-array", FieldValueType.KEYWORD_ARRAY),
    ACTION_GROUPS("action_groups", "\"type\":\"action-groups\"", FieldValueType.KEYWORD_ARRAY),
    ACTION_RULES("action_rules", "\"type\":\"action-rules\"", FieldValueType.KEYWORD_ARRAY),
    ACTION_TYPE("action_type", "\"type\":\"action-type\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Collections.singleton("black"))),
    ACTIONS("actions", "\"type\":\"actions\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList("sms", "mail"))),
    ADR_BGCOLOR("adr_bgcolor", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomColor())),
    ADR_COLOR("adr_color", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomColor())),
    ADR_CONFIDENCE("adr_confidence", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    ADR_FRAME("adr_frame", "", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("481,305,689,312,688,352,480,344")),
    ADR_TEXT("adr_text", "", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomPlate())),
    ADR_TYPE("adr_type", "", FieldValueType.INTEGER, () -> FieldValueType.KEYWORD.valueFromObject(configDependent)),
    ANPR_BGCOLOR("anpr_bgcolor", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomColor())),
    ANPR_COLOR("anpr_color", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomColor())),
    AVERAGE_SPEED("average_speed", "\"type\":\"speed\",\"suffix\":\"km/h\",\"converter\":\"msToKmh\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(10.d, 200.d))),
    AVG_CALC_STATE("avg_calc_state", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("COMPLETED")),
    AVS_SECTION_NAME("avs_section_name", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás")),
    AXLES_HAULER("axles_hauler", "\"type\":\"axle\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(4)),
    AXLES_TRAILER("axles_trailer", "\"type\":\"axle-trailer\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(3)),
    BELT_CONFIDENCE("belt_confidence", "\"type\":\"double\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(1.0d))),
    BELT_RESULT("belt_result", "\"type\":\"seatbelt\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    CATEGORY("category", "\"type\":\"ectn-category\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("210")),
    COUNTRY_LONG("country_long", "\"type\":\"ectn-category\"", FieldValueType.KEYWORD),
    DESCRIPTION("description", "\"type\":\"long-text\"", FieldValueType.TEXT),
    DETECTOR("detector", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("ANPR DETECTOR")),
    DEVICE("device", "\"type\":\"source-type\"", FieldValueType.KEYWORD),

    DIRECTION("direction", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(1)),
    ENTRY_DEVICE_ID("entry_device_id", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("ANPR DETECTOR 01")),
    ENTRY_DEVICE_NAME("entry_device_name", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("ANPR DETECTOR")),
    ENTRY_LOCATION_ID("entry_location_id", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás")),
    ENTRY_LOCATION_NAME("entry_location_name", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás D/2")),
    EXTRA_DATA("extra_data", "\"type\":\"extra-data\"", FieldValueType.TEXT, () -> FieldValueType.KEYWORD.valueFromObject(configDependent)),
    EXTRA_IMAGE("extra_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    FRONT_CUT_IMAGE("front_cut_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    FRONT_PLATE_IMAGE("front_plate_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    HEIGHT("height", "\"type\":\"double\",\"suffix\":\"m\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(1.2d, 2.5d))),
    ID("id", "\"type\":\"event-id\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomEventID())),
    IMAGES("images", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    LANE_ID("lane_id", "", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(6) + 1)),
    LATITUDE("latitude", "\"type\":\"coordinate-lat\",\"coordinateType\":\"gps\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(47.4929403d)),
    LENGTH("length", "\"type\":\"double\",\"suffix\":\"m\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(1.3d, 4.4d))),
    LOCATION("location", "\"type\":\"location\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás")),
    LONGITUDE("longitude", "\"type\":\"coordinate-long\",\"coordinateType\":\"gps\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(19.0214119d)),
    MEASURE_POINT("measure_point", "\"type\":\"measure-point\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás")),
    MMR_CATEGORY("mmr_category", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("CAR")),
    MMR_CATEGORY_CONFIDENCE("mmr_category_confidence", "\"type\":\"integer\",\"suffix\":\"%\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    MMR_COLOR("mmr_color", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("WHITE")),
    MMR_COLOR_CONFIDENCE("mmr_color_confidence", "\"type\":\"integer\",\"suffix\":\"%\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    MMR_MAKE("mmr_make", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Audi")),
    MMR_MODEL("mmr_model", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("TT")),
    MMR_MODEL_CONFIDENCE("mmr_model_confidence", "\"type\":\"integer\",\"suffix\":\"%\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    MMR_SUBMODEL("mmr_submodel", "\"type\":\"text\"", FieldValueType.KEYWORD, nullSupplier),

    NATIONALITY("nationality", "\"type\":\"front-nationality\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomValue(nationalities))),
    OVERVIEW_IMAGE("overview_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    OVERVIEW_PLATE_IMAGE("overview_plate_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    PLATE("plate", "\"type\":\"front-license-plate\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD.valueFromObject(randomValue(nationalities))),
    PLATE_CONFIDENCE("plate_confidence", "\"type\":\"integer\",\"suffix\":\"%\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    PLATE_CONFIDENCE_REAR("plate_confidence_rear", "\"type:\":\"integer\",\"suffix\":\"%\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(100))),
    PLATE_FRAME("plate_frame", "\"type\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("481,305,689,312,688,352,480,344")),
    REAR_NATIONALITY("rear_nationality", "\"type\":\"rear-nationality\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomValue(nationalities))),
    REAR_COUNTRY_LONG("rear_country_long", "", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomValue(nationalities))),
    REAR_CUT_IMAGE("rear_cut_image", "", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    REAR_PLATE("rear_plate", "\"type\":\"rear-license-plate\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(randomPlate())),
    REAR_PLATE_IMAGE("rear_plate_image", "\"type\":\"attachment\",\"attachmentType\":\"image/jpeg\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    REAR_STATE_LONG("rear_state_long", "", FieldValueType.KEYWORD),
    REAR_STATE_SHORT("rear_state_short", "", FieldValueType.KEYWORD),
    REASON("reason", "\"type:\":\"text-array\"", FieldValueType.KEYWORD_ARRAY, nullSupplier),
    SOURCE("source", "\"type:\":\"source\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject("Budapest, Alkotás")),
    SPEED("speed", "\"type:\":\"speed\",\"suffix\":\"km/h\"", FieldValueType.INTEGER, () -> FieldValueType.INTEGER.valueFromObject(randomInt(20, 200))),
    STATE_LONG("state_long", "", FieldValueType.KEYWORD),
    STATE_SHORT("state_short", "", FieldValueType.KEYWORD),
    STORE_TIMESTAMP("store_timestamp", "\"type:\":\"datetime\"", FieldValueType.LONG, () -> {
        return FieldValueType.LONG.valueFromObject(System.currentTimeMillis() - 3_600_000); //one hour before now
    }),
    STRIP_IMAGE("strip_image", "", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    TARGET("target", "\"type:\":\"long-text\"", FieldValueType.TEXT, () -> FieldValueType.TEXT.valueFromObject("""
            {
             "mail": [{
               "to": ["john.doe@company.com", "mary.sue@company.org"],
               "subject": "mySubject"
              }
             ]
            }""")),
    TARGET_DETAILS("target_details", "\"type:\":\"action-target-details\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList("john.doe@company.com", "mary.sue@company.org"))),
    TARGET_FAILED("target_failed", "\"type:\":\"long-text\"", FieldValueType.TEXT, nullSupplier),
    TIMESTAMP("timestamp", "\"type:\":\"datetime\"", FieldValueType.LONG, () -> {
        return FieldValueType.LONG.valueFromObject(System.currentTimeMillis() - 3_600_000); //one hour before now
    }),
    TOLERANCE("tolerance", "\"type:\":\"double\",\"suffix\":\"m\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(0.042d))),
    TYPE("type", "\"type:\":\"text\"", FieldValueType.KEYWORD, () -> FieldValueType.KEYWORD.valueFromObject(configDependent)),
    VIDEO("video", "\"type\":\"attachment\",\"attachmentType\":\"video/mp4\"", FieldValueType.KEYWORD_ARRAY, () -> FieldValueType.KEYWORD_ARRAY.valueFromObject(Arrays.asList(randomEventID(), randomEventID()))),
    WIDTH("width", "\"type\":\"double\",\"suffix\":\"m\"", FieldValueType.DOUBLE, () -> FieldValueType.DOUBLE.valueFromObject(randomDouble(1.2d, 1.9d)));


    private final String fieldName;
    private final String mimeType;
    private final FieldValueType type;
    private final Supplier<? extends Value> value;

    GDSFieldSet(String fieldName, String mimeType, FieldValueType type) {
        this(fieldName, mimeType, type, nullSupplier);
    }

    GDSFieldSet(String fieldName, String mimeType, FieldValueType type, Supplier<? extends Value> value) {

        this.fieldName = fieldName;
        this.mimeType = mimeType;
        this.type = type;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public FieldValueType getType() {
        return type;
    }

    public Value generateValue() {
        return value.get();
    }
}
