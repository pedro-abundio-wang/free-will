package com.free.will.dawn.client.handler;

import com.free.will.common.operation.Operation;
import com.free.will.common.operation.OperationResult;
import com.free.will.common.util.IdUtil;
import com.free.will.dawn.message.RequestMessage;
import com.free.will.dawn.operation.OperationResultFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class RequestMessagePendingCenter {

  private Map<Long, OperationResultFuture> center = new ConcurrentHashMap<>();

  public void put(Long messageId, OperationResultFuture future) {
    this.center.put(messageId, future);
  }

  public void set(Long messageId, OperationResult operationResult) {
    OperationResultFuture operationResultFuture = this.center.get(messageId);
    operationResultFuture.setSuccess(operationResult);
    this.center.remove(messageId);
  }

  public OperationResult get(Long messageId) throws ExecutionException, InterruptedException {
    OperationResultFuture operationResultFuture = this.center.get(messageId);
    return operationResultFuture.get();
  }

  public RequestMessage makeRequestMessage(Operation operation) {
    long messageId = IdUtil.nextId();
    RequestMessage requestMessage = new RequestMessage(messageId, operation);
    OperationResultFuture operationResultFuture = new OperationResultFuture();
    this.put(messageId, operationResultFuture);
    return requestMessage;
  }
}
