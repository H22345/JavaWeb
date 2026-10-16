package SHmarketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import SHmarketplace.dto.LoginDTO;
import SHmarketplace.dto.RegisterDTO;
import SHmarketplace.entity.User;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.mapper.UserMapper;
import SHmarketplace.service.UserService;

/**
 * 用户业务实现
 */
@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterDTO dto) {
        // 1. 账号判重
        if (userMapper.countByUsername(dto.getUsername()) > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 2. 组装实体 + 密码加密
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .avatar("/default/avatar.png")   // 默认头像
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .gender(dto.getGender())
                .birthday(dto.getBirthday())
                .status(1)                       // 默认启用
                .build();

        // 3. 插入（MP 自动填充 createTime/updateTime）
        int rows = userMapper.insert(user);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，请稍后重试");
        }

        // 4. 密码脱敏后返回
        user.setPassword(null);
        return user;
    }

    @Override
    public User login(LoginDTO dto) {
        // 1. 查用户
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);   // 统一错误信息，防止枚举账号
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 4. 密码脱敏
        user.setPassword(null);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
}
