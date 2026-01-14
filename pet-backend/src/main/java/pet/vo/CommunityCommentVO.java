package pet.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityCommentVO {
    private Long id;
    private String content;
    private CommunityUserVO author;
    private LocalDateTime createTime;
}

