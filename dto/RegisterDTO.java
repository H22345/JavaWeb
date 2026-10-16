package SHmarketplace.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户注册请求 DTO，用于接收前端注册表单数据
 * 包含账号、密码、昵称、手机号、邮箱、性别、生日等字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 账号：4-20位字母/数字/下划线 */
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 20, message = "账号长度必须在4-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String username;

    /** 密码：6-32位 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32位之间")
    private String password;

    /** 昵称：2-20位 */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20位之间")
    private String nickname;

    /** 手机号：11位数字 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 性别：0-女 1-男 */
    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别值无效")
    @Max(value = 1, message = "性别值无效")
    private Integer gender;

    /** 生日：不能晚于今天 */
    @NotNull(message = "生日不能为空")
    @PastOrPresent(message = "生日不能晚于今天")
    private LocalDate birthday;
}