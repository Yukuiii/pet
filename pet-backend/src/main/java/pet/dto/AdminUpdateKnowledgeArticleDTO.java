package pet.dto;

import lombok.Data;

@Data
public class AdminUpdateKnowledgeArticleDTO {
    private Long categoryId;
    private String title;
    private String summary;
    private String cover;
    private String content;
}

