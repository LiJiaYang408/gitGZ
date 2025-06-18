package com.example.rearend.mapper;

import com.example.rearend.model.Records;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface RecordsMapper {
    // 根据 goal_name 查询数据
    @Select("SELECT `id`, `goal_name`, `compare_name`, `allowance`, `time`, `original_goal`, `original_compare` FROM `records` WHERE `goal_name` = #{goalName}")
    List<Records> findByGoalName(String goalName);

    // 插入数据
    @Insert("INSERT INTO records (goal_name, compare_name, allowance, time,original_goal,original_compare) VALUES (#{goal_name}, #{compare_name}, #{allowance}, #{time},#{original_goal},#{original_compare})")
    int insert(Records records);

    @Select("SELECT `id`, `goal_name`, `compare_name`, `allowance`, `time`, `original_goal`, `original_compare` FROM `records`")
    List<Records> findAll();
}