package docuTag.global.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageState {

    // 마지막 조회 기준 값 (ex: 마지막 게시글 ID)
    private Long lastId;

    // 한 번에 가져올 데이터 개수
    private int size;

    // 다음 페이지 존재 여부
    private boolean hasNext;

    public PageState(Long lastId, int size, boolean hasNext) {
        this.lastId = lastId;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static PageState of(Long lastId, int size, boolean hasNext) {
        return new PageState(lastId, size, hasNext);
    }
}
