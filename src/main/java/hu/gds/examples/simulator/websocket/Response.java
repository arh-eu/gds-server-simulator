package hu.gds.examples.simulator.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Response {
    private List<byte[]> binaries = new ArrayList<>();
    private int maxDelay = 5_000;
    private static Random random = new Random();

    public Response(List<byte[]> binaries, int maxDelay) {
        this.binaries = binaries;
        this.maxDelay = maxDelay;
    }

    public Response(List<byte[]> binaries) {
        this.binaries = binaries;
    }

    public Response(byte[] binary) {
        this.binaries.add(binary);
    }

    public List<byte[]> getBinaries() {
        return binaries;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public int getNextDelay() {
        return random.nextInt(maxDelay + 1);
    }
}
