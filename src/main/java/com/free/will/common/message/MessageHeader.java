package com.free.will.common.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageHeader {
  private int version;
  private int opCode;
  private long messageId;
}
