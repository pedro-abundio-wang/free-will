package com.free.will.dawn.operation.auth;

import com.free.will.common.operation.OperationResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthOperationResult extends OperationResult {
  private boolean passAuth;
}
