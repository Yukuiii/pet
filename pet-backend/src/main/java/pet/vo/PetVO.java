package pet.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetVO {
    private Long id;
    private String name;
    private String breed;
    private Integer gender;
    private LocalDate birthday;
    private String photo;
}

