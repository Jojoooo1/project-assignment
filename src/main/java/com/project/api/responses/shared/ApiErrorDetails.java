package com.project.api.responses.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.Builder;

@Builder
public record ApiErrorDetails(@JsonInclude(Include.NON_NULL) String pointer, String reason)
    implements Serializable {}
