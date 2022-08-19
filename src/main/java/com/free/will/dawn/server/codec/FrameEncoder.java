package com.free.will.dawn.server.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class FrameEncoder extends LengthFieldPrepender {
  public FrameEncoder() {
    super(FrameParams.LENGTH_FIELD_LENGTH);
  }
}
