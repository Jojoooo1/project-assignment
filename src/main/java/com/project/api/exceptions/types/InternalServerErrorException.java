package com.project.api.exceptions.types;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.project.api.constants.AppConstants;
import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RootException {

  @Serial private static final long serialVersionUID = 694110374288090930L;

  public InternalServerErrorException() {
    super(INTERNAL_SERVER_ERROR, AppConstants.API_DEFAULT_ERROR_MESSAGE);
  }
}
