package com.free.will.dawn.operation.keepalive;

import com.free.will.common.operation.OperationResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeepAliveOperationResult extends OperationResult {
  private long time;
}
