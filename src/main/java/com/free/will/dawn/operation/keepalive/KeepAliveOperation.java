package com.free.will.dawn.operation.keepalive;

import com.free.will.common.operation.Operation;
import lombok.Data;

@Data
public class KeepAliveOperation extends Operation {

  private long time;

  public KeepAliveOperation() {
    this.time = System.nanoTime();
  }

  @Override
  public KeepAliveOperationResult execute() {
    return KeepAliveOperationResult.builder().time(time).build();
  }
}
