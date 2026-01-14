package pet.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetUpdateDTO {
    private String name;
    private String breed;
    private Integer gender;
    private LocalDate birthday;
    private String photo;
}

