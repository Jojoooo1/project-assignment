package com.project.api.exceptions.types;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = UNAUTHORIZED)
public class NotAuthorizedException extends RootException {

  @Serial private static final long serialVersionUID = -711441617476620028L;

  public NotAuthorizedException() {
    super(UNAUTHORIZED);
  }
}
