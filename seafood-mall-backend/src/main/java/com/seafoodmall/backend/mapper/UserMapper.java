/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafoodmall.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
 
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0")
    Long getTotalUserCount();

    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND created_time >= #{startOfMonth}")
    Long getMonthlyNewUsers(@Param("startOfMonth") LocalDateTime startOfMonth);
} 