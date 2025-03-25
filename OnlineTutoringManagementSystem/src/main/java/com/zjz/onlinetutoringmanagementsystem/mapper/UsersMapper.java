package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
public interface UsersMapper extends BaseMapper<Users> {

    /*
    return id
     */
    @Select("select user_id from users where username LIKE #{userName}")
    Integer selectIdByUserName(String userName);

    /*
    return all
     */
    @Select("select user_id,username,password,role,enabled from users where username = #{username} ")
    Users selectByUsername(String username);


    /*
          根据用户名和密码查询
         * @param user
         **/
    @Select("select user_id, username, password, role from users where username = #{username} and password = #{password}")
    Users getByUsernameAndPassword(Users user);




}
