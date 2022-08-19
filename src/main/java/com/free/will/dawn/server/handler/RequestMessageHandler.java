package com.free.will.dawn.server.handler;

import com.free.will.common.operation.Operation;
import com.free.will.common.operation.OperationResult;
import com.free.will.dawn.message.RequestMessage;
import com.free.will.dawn.message.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestMessageHandler extends SimpleChannelInboundHandler<RequestMessage> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage)
      throws Exception {
    Operation operation = requestMessage.getMessageBody();
    OperationResult operationResult = operation.execute();

    ResponseMessage responseMessage = new ResponseMessage();
    responseMessage.setMessageHeader(requestMessage.getMessageHeader());
    responseMessage.setMessageBody(operationResult);

    ctx.writeAndFlush(responseMessage);
  }
}
