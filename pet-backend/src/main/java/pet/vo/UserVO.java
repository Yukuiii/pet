package pet.vo;

import lombok.Data;

/**
 * 用户信息VO（返回给前端的用户数据）
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户角色：user/admin
     */
    private String role;
}
