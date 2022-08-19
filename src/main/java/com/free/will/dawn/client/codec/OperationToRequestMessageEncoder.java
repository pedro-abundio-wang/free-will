package com.free.will.dawn.client.codec;

import com.free.will.common.operation.Operation;
import com.free.will.common.util.IdUtil;
import com.free.will.dawn.message.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
  @Override
  protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out)
      throws Exception {
    RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
    out.add(requestMessage);
  }
}
