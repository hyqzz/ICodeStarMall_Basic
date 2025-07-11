/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafoodmall.backend.dto.RecentOrderDTO;
import com.seafoodmall.backend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
 
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT SUM(total_amount) FROM `order` WHERE deleted = 0")
    BigDecimal getTotalSalesAmount();

    @Select("SELECT COUNT(*) FROM `order` WHERE deleted = 0")
    Long getTotalOrderCount();

    @Select("" +
            "SELECT " +
            "  o.order_no as id, " +
            "  COALESCE(u.real_name, u.username) as customer, " +
            "  o.total_amount as amount, " +
            "  o.status as status " +
            "FROM `order` o " +
            "JOIN `user` u ON o.user_id = u.id " +
            "WHERE o.deleted = 0 " +
            "ORDER BY o.created_time DESC " +
            "LIMIT 4")
    List<RecentOrderDTO> getRecentOrders();
} 