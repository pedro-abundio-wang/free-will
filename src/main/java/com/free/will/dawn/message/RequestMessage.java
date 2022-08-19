package com.free.will.dawn.message;

import com.free.will.common.message.Message;
import com.free.will.common.message.MessageHeader;
import com.free.will.common.operation.Operation;
import com.free.will.dawn.operation.OperationType;

public class RequestMessage extends Message<Operation> {

  public RequestMessage() {}

  public RequestMessage(Long messageId, Operation operation) {
    MessageHeader messageHeader =
        MessageHeader.builder()
            .messageId(messageId)
            .opCode(OperationType.fromOperation(operation).getOpCode())
            .build();
    this.setMessageHeader(messageHeader);
    this.setMessageBody(operation);
  }

  @Override
  public Class getMessageBodyDecodeClass(int opCode) {
    return OperationType.fromOpCode(opCode).getOperationClazz();
  }
}
