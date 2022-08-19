package com.free.will.dawn.operation;

import com.free.will.common.operation.Operation;
import com.free.will.common.operation.OperationResult;
import com.free.will.dawn.operation.auth.AuthOperation;
import com.free.will.dawn.operation.auth.AuthOperationResult;
import com.free.will.dawn.operation.keepalive.KeepAliveOperation;
import com.free.will.dawn.operation.keepalive.KeepAliveOperationResult;

import java.util.function.Predicate;

public enum OperationType {
  AUTH(1, AuthOperation.class, AuthOperationResult.class),
  KEEPALIVE(2, KeepAliveOperation.class, KeepAliveOperationResult.class);

  private int opCode;
  private Class<? extends Operation> operationClazz;
  private Class<? extends OperationResult> operationResultClazz;

  OperationType(
      int opCode,
      Class<? extends Operation> operationClazz,
      Class<? extends OperationResult> responseClass) {
    this.opCode = opCode;
    this.operationClazz = operationClazz;
    this.operationResultClazz = responseClass;
  }

  public int getOpCode() {
    return opCode;
  }

  public Class<? extends Operation> getOperationClazz() {
    return operationClazz;
  }

  public Class<? extends OperationResult> getOperationResultClazz() {
    return operationResultClazz;
  }

  public static OperationType fromOpCode(int opCode) {
    return getOperationType(operationType -> operationType.opCode == opCode);
  }

  public static OperationType fromOperation(Operation operation) {
    return getOperationType(operationType -> operationType.operationClazz == operation.getClass());
  }

  private static OperationType getOperationType(Predicate<OperationType> predicate) {
    OperationType[] values = values();
    for (OperationType operationType : values) {
      if (predicate.test(operationType)) {
        return operationType;
      }
    }
    throw new AssertionError("NOT_FOUND_OPERATION_TYPE");
  }
}
