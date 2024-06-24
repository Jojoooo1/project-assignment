package com.project.api.responses.shared;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record ApiListPaginationSuccess<T>(
    PaginationMeta<T> meta, Collection<T> data, PaginationLink<T> links) {

  public ApiListPaginationSuccess(final Page<T> page) {
    this(new PaginationMeta<>(page), page.getContent(), new PaginationLink<>(page));
  }

  public static <T> ApiListPaginationSuccess<T> of(final Page<T> page) {
    return new ApiListPaginationSuccess<>(page);
  }

  public record PaginationMeta<T>(
      Integer currentPage, Integer pageSize, Integer totalPages, Long totalItems, String sortedBy) {

    public PaginationMeta(final Page<T> page) {
      this(
          page.getNumber(),
          page.getSize(),
          page.getTotalPages(),
          page.getTotalElements(),
          page.getSort().isSorted() ? page.getSort().toString() : "");
    }
  }

  public record PaginationLink<T>(
      String self, String first, String last, String next, String previous) {

    public static final String PAGE_PARAMS = "page";
    public static final String SIZE_PARAMS = "size";
    public static final String SORT_PARAMS = "sort";

    public PaginationLink(final Page<T> page) {
      this(
          createBuilder()
              .queryParam(PAGE_PARAMS, page.getNumber())
              .queryParam(SIZE_PARAMS, page.getSize())
              .query(sortString(page.getSort()))
              .build()
              .toUriString(),
          createBuilder()
              .queryParam(PAGE_PARAMS, 0)
              .queryParam(SIZE_PARAMS, page.getSize())
              .query(sortString(page.getSort()))
              .build()
              .toUriString(),
          createBuilder()
              .queryParam(PAGE_PARAMS, page.getTotalPages() - 1)
              .queryParam(SIZE_PARAMS, page.getSize())
              .query(sortString(page.getSort()))
              .build()
              .toUriString(),
          !page.isFirst()
              ? createBuilder()
                  .queryParam(PAGE_PARAMS, page.getNumber() - 1)
                  .queryParam(SIZE_PARAMS, page.getSize())
                  .query(sortString(page.getSort()))
                  .build()
                  .toUriString()
              : "",
          page.hasNext()
              ? createBuilder()
                  .queryParam(PAGE_PARAMS, page.getNumber() + 1)
                  .queryParam(SIZE_PARAMS, page.getSize())
                  .query(sortString(page.getSort()))
                  .build()
                  .toUriString()
              : "");
    }

    private static ServletUriComponentsBuilder createBuilder() {
      return ServletUriComponentsBuilder.fromCurrentRequestUri();
    }

    private static String sortString(final Sort sort) {
      final StringBuilder ans = new StringBuilder();

      for (final Sort.Order s : sort) {
        ans.append("&" + SORT_PARAMS + "=");
        ans.append(URLEncoder.encode(s.getProperty(), StandardCharsets.UTF_8));
        ans.append(",");
        ans.append(
            URLEncoder.encode(s.getDirection().name().toLowerCase(), StandardCharsets.UTF_8));
      }

      return ans.toString();
    }
  }
}
