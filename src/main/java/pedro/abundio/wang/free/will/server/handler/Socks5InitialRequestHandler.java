package pedro.abundio.wang.free.will.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import io.netty.util.ReferenceCountUtil;

public class Socks5InitialRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        boolean release = true;

        try {
            if (msg instanceof DefaultSocks5InitialRequest) {
                DefaultSocks5InitialRequest request = (DefaultSocks5InitialRequest) msg;
                if(request.decoderResult().isFailure()) {
                    ctx.fireChannelRead(request);
                } else {
                    if(request.version().equals(SocksVersion.SOCKS5)) {
                        Socks5InitialResponse initialResponse = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
                        ctx.writeAndFlush(initialResponse);
                    }
                }
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }

        }

    }
}
