package pet.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommunityPostVO {
    private Long id;
    private String content;
    private List<String> images;
    private CommunityUserVO author;
    private LocalDateTime createTime;
}

