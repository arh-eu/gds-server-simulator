package hu.gds.examples.simulator.websocket;

import java.util.ArrayList;
import java.util.List;

import static hu.gds.examples.simulator.RandomUtil.RANDOM;

public class Response {
    private final List<byte[]> binaries;
    private final int maxDelay;
    private final boolean closeConnection;

    public Response(List<byte[]> binaries) {
        this(binaries, 5000);
    }

    public Response(List<byte[]> binaries, int maxDelay) {
        this(binaries, maxDelay, false);
    }

    public Response(List<byte[]> binaries, boolean closeConnection) {
        this(binaries, 5000, closeConnection);
    }

    public Response(List<byte[]> binaries, int maxDelay, boolean closeConnection) {
        this.binaries = binaries;
        this.maxDelay = maxDelay;
        this.closeConnection = closeConnection;
    }

    public Response(byte[] binary) {
        this(new ArrayList<>());
        this.binaries.add(binary);
    }

    public List<byte[]> getBinaries() {
        return binaries;
    }

    public boolean shouldCloseConnection() {
        return closeConnection;
    }

    public int getNextDelay() {
        return RANDOM.nextInt(maxDelay + 1);
    }
}
