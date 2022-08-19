package com.free.will.dawn.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
  public FrameDecoder() {
    super(
        Integer.MAX_VALUE,
        0,
        FrameParams.LENGTH_FIELD_LENGTH,
        0,
        FrameParams.INITIAL_BYTES_TO_STRIP);
  }
}
