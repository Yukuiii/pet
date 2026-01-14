package pet.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
