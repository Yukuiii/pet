package pet.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthRecordVO {
    private Long id;
    private LocalDateTime recordTime;
    private String title;
    private String content;
}

