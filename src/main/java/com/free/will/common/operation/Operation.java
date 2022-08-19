package com.free.will.common.operation;

import com.free.will.common.message.MessageBody;

public abstract class Operation extends MessageBody {
  public abstract OperationResult execute();
}
