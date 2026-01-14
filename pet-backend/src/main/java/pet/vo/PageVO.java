package pet.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    private Long total;
    private List<T> list;

    public static <T> PageVO<T> of(long total, List<T> list) {
        PageVO<T> vo = new PageVO<>();
        vo.setTotal(total);
        vo.setList(list);
        return vo;
    }
}

