package pet.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeArticleVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String cover;
    private LocalDateTime createTime;
    private String content;
}

