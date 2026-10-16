package SHmarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import SHmarketplace.entity.User;

/**
 * 用户数据访问接口
 * 继承 BaseMapper 获得 MyBatis-Plus 通用 CRUD 能力
 * 自定义 SQL 在 UserMapper.xml 中实现
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 用户注册（新增用户）
     *
     * @param user 用户信息
     * @return 影响行数（1-注册成功，0-失败）
     */
    int register(User user);

    /**
     * 用户登录（根据账号查询用户）
     *
     * @param username 账号
     * @return 用户信息（密码校验在 Service 层做）
     */
    User findByUsername(@Param("username") String username);

    /**
     * 检查账号是否已存在（注册时判重）
     *
     * @param username 账号
     * @return 存在的用户数（>0 表示已占用）
     */
    int countByUsername(@Param("username") String username);
}