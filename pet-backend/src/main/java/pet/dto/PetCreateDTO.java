package pet.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetCreateDTO {
    private String name;
    private String breed;
    private Integer gender;
    private LocalDate birthday;
}

