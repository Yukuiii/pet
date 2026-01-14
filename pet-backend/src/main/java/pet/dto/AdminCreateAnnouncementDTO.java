package pet.dto;

import lombok.Data;

@Data
public class AdminCreateAnnouncementDTO {
    private String title;
    private String content;
    private Integer status;
}

