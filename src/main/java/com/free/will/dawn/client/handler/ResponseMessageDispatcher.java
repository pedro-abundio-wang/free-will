package com.free.will.dawn.client.handler;

import com.free.will.dawn.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseMessageDispatcher extends SimpleChannelInboundHandler<ResponseMessage> {

  private RequestMessagePendingCenter requestMessagePendingCenter;

  public ResponseMessageDispatcher(RequestMessagePendingCenter requestMessagePendingCenter) {
    this.requestMessagePendingCenter = requestMessagePendingCenter;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage responseMessage)
      throws Exception {
    requestMessagePendingCenter.set(
        responseMessage.getMessageHeader().getMessageId(), responseMessage.getMessageBody());
  }
}
