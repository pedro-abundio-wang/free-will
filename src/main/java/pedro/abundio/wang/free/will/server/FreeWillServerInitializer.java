package pedro.abundio.wang.free.will.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import pedro.abundio.wang.free.will.server.handler.Socks5CommandRequestHandler;
import pedro.abundio.wang.free.will.server.handler.Socks5InitialRequestHandler;

public class FreeWillServerInitializer extends ChannelInitializer<SocketChannel> {

    private FreeWillServer freeWillServer;

    public FreeWillServerInitializer(FreeWillServer freeWillServer) {
        this.freeWillServer = freeWillServer;
    }

    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new IdleStateHandler(5, 30, 0));
        channel.pipeline().addLast(Socks5ServerEncoder.DEFAULT);
        channel.pipeline().addLast(new Socks5InitialRequestDecoder());
        channel.pipeline().addLast(new Socks5InitialRequestHandler());
        channel.pipeline().addLast(new Socks5CommandRequestDecoder());
        channel.pipeline().addLast(new Socks5CommandRequestHandler(freeWillServer.getBossGroup()));
    }
}
