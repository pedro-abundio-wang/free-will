package com.free.will.common.message;

import com.free.will.common.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public abstract class Message<T extends MessageBody> {

  private MessageHeader messageHeader;
  private T messageBody;

  public T getMessageBody() {
    return messageBody;
  }

  public void encode(ByteBuf byteBuf) {
    byteBuf.writeInt(messageHeader.getVersion());
    byteBuf.writeInt(messageHeader.getOpCode());
    byteBuf.writeLong(messageHeader.getMessageId());
    byteBuf.writeBytes(JsonUtil.toJson(messageBody).getBytes());
  }

  public void decode(ByteBuf byteBuf) {
    int version = byteBuf.readInt();
    int opCode = byteBuf.readInt();
    long messageId = byteBuf.readLong();
    this.messageHeader =
        MessageHeader.builder().version(version).opCode(opCode).messageId(messageId).build();
    Class<T> bodyClazz = getMessageBodyDecodeClass(opCode);
    this.messageBody = JsonUtil.fromJson(byteBuf.toString(StandardCharsets.UTF_8), bodyClazz);
  }

  public abstract Class<T> getMessageBodyDecodeClass(int opCode);
}
