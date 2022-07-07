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

import hu.gds.examples.simulator.GDSSimulator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.NotYetConnectedException;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof BinaryWebSocketFrame) {
            byte[] request = new byte[frame.content().readableBytes()];
            frame.content().readBytes(request);
            try {

                Response response = GDSSimulator.handleRequest(ctx, ctx.channel().id().asLongText(), request);
                if (response == null) {
                    return;
                }

                if (response.getBinaries().size() == 1) {
                    byte[] binary = response.getBinaries().get(0);
                    if (binary != null) {
                        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
                    }
                } else {
                    for (byte[] binary : response.getBinaries()) {
                        if (binary != null) {
                            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
                        }
                        Thread.sleep(response.getNextDelay());
                    }
                }

                if (response.shouldCloseConnection()) {
                    closeConnection(ctx);
                }
            } catch (AlreadyConnectedException | NotYetConnectedException exc) {
                closeConnection(ctx);
            } catch (Throwable t) {
                throw new IllegalStateException(t);
            }
        } else if (frame instanceof CloseWebSocketFrame) {
            closeConnection(ctx);
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame());
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeConnection(ctx);
    }

    private void closeConnection(ChannelHandlerContext ctx) {
        String uuid = ctx.channel().id().asLongText();
        GDSSimulator.connectionClosed(uuid);
        ctx.channel().writeAndFlush(new CloseWebSocketFrame()).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeConnection(ctx);
    }
}
