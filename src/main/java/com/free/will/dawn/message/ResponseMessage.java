package com.free.will.dawn.message;

import com.free.will.common.message.Message;
import com.free.will.common.operation.OperationResult;
import com.free.will.dawn.operation.OperationType;

public class ResponseMessage extends Message<OperationResult> {
  @Override
  public Class getMessageBodyDecodeClass(int opCode) {
    return OperationType.fromOpCode(opCode).getOperationResultClazz();
  }
}
