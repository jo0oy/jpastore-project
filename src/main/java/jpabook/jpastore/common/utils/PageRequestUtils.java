package jpabook.jpastore.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageRequestUtils {

    // PageRequest pageNumber 재정의 (0부터 시작, 클라이언트로부터는 1부터 전달 받는다)
    public static PageRequest of(Pageable pageable) {
        int page = pageable.getPageNumber();
        if(page < 0) page = 0; // 0 미만일 경우, 0으로 재설정
        else if(page > 0) page -= 1; // 1부터 전달받기 때문에 -1씩 해준다.

        return PageRequest.of(page, pageable.getPageSize(), pageable.getSort());
    }
}
