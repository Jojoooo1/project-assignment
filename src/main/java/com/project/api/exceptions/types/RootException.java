package com.project.api.exceptions.types;

import com.project.api.responses.shared.ApiErrorDetails;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Getter
public class RootException extends RuntimeException {

  @Serial private static final long serialVersionUID = 6378336966214073013L;

  private final HttpStatus httpStatus;
  private final List<ApiErrorDetails> errors = new ArrayList<>();

  public RootException(@NonNull final HttpStatus httpStatus) {
    super();
    this.httpStatus = httpStatus;
  }

  public RootException(@NonNull final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
