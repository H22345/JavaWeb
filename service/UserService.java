package SHmarketplace.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import SHmarketplace.dto.LoginDTO;
import SHmarketplace.dto.RegisterDTO;
import SHmarketplace.entity.User;

/**
 * 用户业务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(@Valid RegisterDTO dto);

    /**
     * 用户登录 — 返回用户信息（密码已脱敏）
     */
    User login(@Valid LoginDTO dto);

    /**
     * 根据账号查询用户（内部用，可能返回 null）
     */
    User findByUsername(@NotBlank String username);

    /**
     * 根据 ID 查询用户（可能返回 null）
     */
    User findById(Long id);
}