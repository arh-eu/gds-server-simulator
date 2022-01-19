/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the GDS Simulator project.
 * Budapest, 2020/01/27
 */

package hu.gds.examples.simulator;

import hu.gds.examples.simulator.websocket.WebSocketServer;

import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        final AtomicReference<WebSocketServer> wss = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try (WebSocketServer instance = new WebSocketServer()) {
                wss.set(instance);
                instance.run();
            }
        });
        t.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            wss.get().close();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }
}
