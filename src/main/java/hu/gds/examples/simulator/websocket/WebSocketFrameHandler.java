/*
 * Intellectual property of ARH Inc.
 * This file belongs to the GDS 5.0 system in the gdsserversimulator project.
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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof BinaryWebSocketFrame) {
            byte[] request = new byte[frame.content().readableBytes()];
            frame.content().readBytes(request);
            try {

                Response response = GDSSimulator.handleRequest(request);
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

            } catch (Throwable t) {
                throw new IllegalStateException(t);
            }
        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.channel().close();
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //
        ctx.close();
    }
}
