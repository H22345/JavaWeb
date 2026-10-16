package SHmarketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import SHmarketplace.common.Result;
import SHmarketplace.dto.LoginDTO;
import SHmarketplace.dto.RegisterDTO;
import SHmarketplace.entity.User;
import SHmarketplace.service.UserService;
import SHmarketplace.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 */
@Tag(name = "用户认证", description = "注册、登录等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        User user = userService.login(dto);
        // 登录成功后签发 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }
}
