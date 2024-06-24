package com.project.api.exceptions.types;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = BAD_REQUEST)
public class BadRequestException extends RootException {

  @Serial private static final long serialVersionUID = -4391321810681152889L;

  public BadRequestException(final String message) {
    super(BAD_REQUEST, message);
  }
}
