package com.free.will.dawn.operation.auth;

import com.free.will.common.operation.Operation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class AuthOperation extends Operation {

  private static final Logger logger = LoggerFactory.getLogger(AuthOperation.class);

  private static final String USERNAME = "admin";

  private String userName;
  private String password;

  public AuthOperation(String userName) {
    super();
    this.userName = userName;
  }

  @Override
  public AuthOperationResult execute() {
    logger.info("begin auth");
    if (USERNAME.equalsIgnoreCase(this.userName)) {
      return AuthOperationResult.builder().passAuth(true).build();
    }
    return AuthOperationResult.builder().passAuth(false).build();
  }
}
