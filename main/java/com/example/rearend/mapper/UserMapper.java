package com.example.rearend.mapper;


import com.example.rearend.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT `userId`, `userName`, `password`, `userRole` FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT users values (0,#{username},#{password},'admin')")
    Integer signINUser(User user);
}