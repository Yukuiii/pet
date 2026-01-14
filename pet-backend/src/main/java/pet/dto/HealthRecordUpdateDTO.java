package pet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthRecordUpdateDTO {
    private LocalDateTime recordTime;
    private String title;
    private String content;
}

