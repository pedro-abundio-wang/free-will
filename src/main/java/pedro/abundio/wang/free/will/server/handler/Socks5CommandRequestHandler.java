package pedro.abundio.wang.free.will.server.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

public class Socks5CommandRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = Logger.getLogger(Socks5CommandRequestHandler.class);

    private EventLoopGroup bossGroup;

    public Socks5CommandRequestHandler(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
    }

    @Override
    public void channelRead(final ChannelHandlerContext clientChannelContext, Object msg) throws Exception {

        boolean release = true;

        try {
            if (msg instanceof DefaultSocks5CommandRequest) {
                DefaultSocks5CommandRequest request = (DefaultSocks5CommandRequest) msg;

                if(request.type().equals(Socks5CommandType.CONNECT)) {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(bossGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new Send2RemoteHandler(clientChannelContext));
                                }
                            });

                    log.info("connect to remote address = " + request.dstAddr() +  " ,remote port = " + request.dstPort());
                    ChannelFuture future = bootstrap.connect(request.dstAddr(), request.dstPort());

                    future.addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if(future.isSuccess()) {
                                clientChannelContext.pipeline().addLast(new Back2ClientHandler(future));
                                Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                                clientChannelContext.writeAndFlush(commandResponse);
                            } else {
                                Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                                clientChannelContext.writeAndFlush(commandResponse);
                            }
                        }
                    });
                } else {
                    log.info("dispatch none-connect request, request type = " + request.type());
                    clientChannelContext.fireChannelRead(msg);
                }
            } else {
                log.info("Msg is not a DefaultSocks5CommandRequest instance.");
                release = false;
                clientChannelContext.fireChannelRead(msg);
            }
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }

        }

    }

    private static class Send2RemoteHandler extends ChannelInboundHandlerAdapter {

        private ChannelHandlerContext clientChannelContext;

        public Send2RemoteHandler(ChannelHandlerContext clientChannelContext) {
            this.clientChannelContext = clientChannelContext;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object destMsg) throws Exception {
            clientChannelContext.writeAndFlush(destMsg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            clientChannelContext.channel().close();
        }
    }

    private static class Back2ClientHandler extends ChannelInboundHandlerAdapter {

        private ChannelFuture destChannelFuture;

        public Back2ClientHandler(ChannelFuture destChannelFuture) {
            this.destChannelFuture = destChannelFuture;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            destChannelFuture.channel().writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            destChannelFuture.channel().close();
        }
    }

}
