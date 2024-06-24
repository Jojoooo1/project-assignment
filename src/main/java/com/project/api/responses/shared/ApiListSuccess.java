package com.project.api.responses.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ApiListSuccess<T>(List<T> data) {

  public static <T, S extends Collection<T>> ApiListSuccess<T> of(final S data) {
    return new ApiListSuccess<>(new ArrayList<>(data));
  }

  public static <T> ApiListSuccess<T> of(final List<T> data) {
    return new ApiListSuccess<>(data);
  }
}
