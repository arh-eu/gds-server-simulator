/*
 * Intellectual property of Adaptive Recognition.
 * This file belongs to the GDS 5 system in the GDS Simulator project.
 * Budapest, 2020/01/27
 */


/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package hu.gds.examples.simulator.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * An HTTP server which serves GDS's WebSocket requests at: {@linkplain "http://localhost:{8443|8888}/gate"}
 */
public final class WebSocketServer implements Runnable, AutoCloseable {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8888"));
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel ch;
    private volatile boolean isRunning;


    public WebSocketServer() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void run() {
        try {
            // Configure SSL.
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketServerInitializer(sslCtx));

            ch = b.bind(PORT).sync().channel();

            System.out.println("To use the Simulator connect to this at the address " +
                    (SSL ? "wss" : "ws") + "://127.0.0.1:" + PORT + "/gate");
            isRunning = true;
            ch.closeFuture().sync();
        } catch (Throwable reason) {
            throw new RuntimeException(reason);
        }
    }

    @Override
    public void close() {
        if (isRunning) {
            isRunning = false;
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
                ch.closeFuture().sync();
                System.err.println("WebSocket server stopped.");
            } catch (Throwable cause) {
                throw new RuntimeException(cause);
            }
        }
    }
}
