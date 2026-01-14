package pet.dto;

import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录类型：user-用户登录，admin-管理员登录
     */
    private String loginRole;
}
