package guru.springframework.spring6restmvc.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableUtil {
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PAGE_NUMBER_S = "0";
    public static final String DEFAULT_PAGE_SIZE_S = "10";

    public static Pageable buildPageable(Integer pageNumber, Integer pageSize) {
        int queryPageNumber = pageNumber == null || pageSize < DEFAULT_PAGE_NUMBER ? DEFAULT_PAGE_NUMBER : pageNumber;
        int queryPageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;

        return PageRequest.of(queryPageNumber, queryPageSize);
    }
}
