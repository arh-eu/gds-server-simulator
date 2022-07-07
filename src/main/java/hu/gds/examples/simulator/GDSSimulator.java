/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the gds-server-simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator;

import hu.arheu.gds.message.FullGdsMessage;
import hu.arheu.gds.message.data.MessageData;
import hu.arheu.gds.message.data.MessageDataType;
import hu.arheu.gds.message.errors.ValidationException;
import hu.arheu.gds.message.header.MessageHeaderBase;
import hu.gds.examples.simulator.responses.ResponseGenerator;
import hu.gds.examples.simulator.websocket.Response;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.NotYetConnectedException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;


public class GDSSimulator {
    private static final Logger LOGGER = Logger.getLogger("GDSSimulator");

    private static final List<MessageDataType> allowFailuresFor = Arrays.asList(
            MessageDataType.EVENT_2,
            MessageDataType.ATTACHMENT_REQUEST_4,
            MessageDataType.ATTACHMENT_RESPONSE_6,
            MessageDataType.EVENT_DOCUMENT_8,
            MessageDataType.QUERY_REQUEST_10,
            MessageDataType.NEXT_QUERY_PAGE_12
    );

    public static void setErrorPercentage(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("The error percentage should be in the [0..100] range! Specified: " + value);
        }
        errorPercentage = value;
        if (errorPercentage > 0) {
            LOGGER.info("The GDS Simulator will have " + errorPercentage + "% chance to reply to your requests with an error message.");
        } else {
            LOGGER.info("The GDS Simulator will always reply to your requests with success (if it is a valid GDS message).");
        }
    }

    public static Response handleRequest(ChannelHandlerContext ctx, String uuid, byte[] request) throws IOException, ValidationException {

        FullGdsMessage fullGdsMessage = new FullGdsMessage(request);
        MessageHeaderBase requestHeader = fullGdsMessage.getHeader();
        MessageData requestData = fullGdsMessage.getData();

        MessageDataType messageDataType = requestData.getMessageDataType();
        LOGGER.info("GDS has received a message of type '" + messageDataType.name() + "'..");

        //this WebSocket connection does not have an active session alive (no login prior to this message on this connection)
        if (messageDataType != MessageDataType.CONNECTION_0 && !hasLoggedIn(requestHeader.getUserName())) {
            throw new NotYetConnectedException();
        }

        //this WebSocket connection already has an active session alive.
        if (messageDataType == MessageDataType.CONNECTION_0 && hasConnection(uuid)) {
            throw new AlreadyConnectedException();
        }

        if (allowFailuresFor.contains(messageDataType) && RANDOM.nextInt(100) < errorPercentage) {
            LOGGER.info("Creating an automated response for an invalid request");
            return ResponseGenerator.getInvalidRequestMessage(requestHeader, messageDataType);
        }

        Response response;
        switch (messageDataType) {
            case CONNECTION_0:
                response = ResponseGenerator.getConnectionAckMessage(ctx, uuid, requestHeader, requestData.asConnectionMessageData0());
                LOGGER.info("Sending back the CONNECTION_ACK..");
                break;
            case EVENT_2:
                response = ResponseGenerator.getEventAckMessage(requestHeader);
                LOGGER.info("Sending back the EVENT_ACK..");
                break;
            case ATTACHMENT_REQUEST_4:
                response = ResponseGenerator.getAttachmentRequestAckMessage(requestHeader);
                LOGGER.info("Sending back the ATTACHMENT_REQUEST_ACK..");
                break;
            case ATTACHMENT_RESPONSE_6:
                response = ResponseGenerator.getAttachmentResponseAckMessage(requestHeader);
                LOGGER.info("Sending back the ATTACHMENT_RESPONSE_ACK..");
                break;
            case EVENT_DOCUMENT_8:
                response = ResponseGenerator.getEventDocumentAckMessage(requestHeader);
                LOGGER.info("Sending back the EVENT_DOCUMENT_ACK..");
                break;
            case QUERY_REQUEST_10:
                response = ResponseGenerator.getQueryRequestAckMessage(requestHeader, true);
                LOGGER.info("Sending back the QUERY_REQUEST_ACK..");
                break;
            case NEXT_QUERY_PAGE_12:
                response = ResponseGenerator.getQueryRequestAckMessage(requestHeader, false);
                LOGGER.info("Sending back the QUERY_REQUEST_ACK..");
                break;
            case ATTACHMENT_REQUEST_ACK_5:
            case ATTACHMENT_RESPONSE_ACK_7:
            case EVENT_DOCUMENT_ACK_9:
                return null;
            default:
                response = ResponseGenerator.getInvalidAckMessage(requestHeader, messageDataType);
                break;
        }
        LOGGER.info("GDS Response successfully sent!");
        return response;
    }

    private static final Properties SETTINGS = new Properties();

    private static final Set<String> REGISTERED_USERS = new HashSet<>();
    private static final Set<String> EVENT_USERS = new HashSet<>();
    public static int PORT = 8888;
    public static int PUSH_INTERVAL_MILLIS;
    private static int errorPercentage;

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {

            @Override
            public synchronized String format(LogRecord lr) {
                String format = "[%1$tF %1$tT] [%2$s] | %3$s::%4$s | %5$s %n";
                return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(), lr.getSourceClassName(), lr.getSourceMethodName(), lr.getMessage());
            }
        });

        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);

        REGISTERED_USERS.add("user");

        try {
            SETTINGS.load(new FileInputStream("settings.properties"));

            Arrays.asList(SETTINGS.getProperty("users", "").split(", ")).forEach(GDSSimulator::addUser);
            setErrorPercentage(Integer.parseInt(SETTINGS.getProperty("error_percentage", "10")));
            PORT = Integer.parseInt(SETTINGS.getProperty("port", "8888"));
            Arrays.asList(SETTINGS.getProperty("send_push_messages_to", "").split(", ")).forEach(GDSSimulator::addEventUser);
            PUSH_INTERVAL_MILLIS = Integer.parseInt(SETTINGS.getProperty("push_message_interval", "10000"));

        } catch (FileNotFoundException e) {
            System.err.println("'settings.properties' file not found, using default values..");
            setErrorPercentage(10);
        } catch (Throwable throwable) {
            System.err.println("Invalid 'settings.properties' file. Please fix it before running the simulator!\nReason: " + throwable);
            System.exit(1);
        }
        System.err.println("GDS Simulator initialized!");
    }


    public static boolean hasUser(String user) {
        return REGISTERED_USERS.contains(user);
    }

    public static void addUser(String user) {
        REGISTERED_USERS.add(user);
    }

    public static void addEventUser(String user) {
        if (!hasUser(user)) {
            throw new IllegalStateException("The user named %1$s is not registered!".formatted(user));
        }
        EVENT_USERS.add(user);
    }

    public static boolean hasEventUser(String user) {
        return EVENT_USERS.contains(user);
    }


    public static void removeUser(String user) {
        REGISTERED_USERS.remove(user);
    }

    public static Set<String> getRegisteredUsers() {
        return new HashSet<>(REGISTERED_USERS);
    }

    // connection UUID -> username
    private final static Map<String, String> logins = new HashMap<>();

    public static boolean hasConnection(String uuid) {
        return logins.containsKey(uuid);
    }

    public static boolean hasLoggedIn(String user) {
        return logins.containsValue(user);
    }

    public static void setLoggedIn(String uuid, String user) {
        logins.put(uuid, user);
        System.err.println("Connection towards " + uuid + " has been established.");
    }

    public static void connectionClosed(String uuid) {
        logins.remove(uuid);
        ResponseGenerator.stopEventPushing(uuid);
        System.err.println("Connection towards " + uuid + " has been closed.");
    }
}
