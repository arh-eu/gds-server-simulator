/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gdsserversimulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator;

import hu.gds.examples.simulator.websocket.WebSocketServer;

public class Main {
    public static void main(String[] args) {
        GDSSimulator.setErrorPercentage(0);
        new WebSocketServer().run();
    }
}
