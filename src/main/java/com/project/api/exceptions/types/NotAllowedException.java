package com.project.api.exceptions.types;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = FORBIDDEN)
public class NotAllowedException extends RootException {

  @Serial private static final long serialVersionUID = -1774388035625344059L;

  public NotAllowedException() {
    super(FORBIDDEN);
  }
}
