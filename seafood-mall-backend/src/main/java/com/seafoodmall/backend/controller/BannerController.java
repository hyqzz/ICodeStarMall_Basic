/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.Banner;
import com.seafoodmall.backend.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    // H5端获取所有启用的banner
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> list = bannerService.list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
        );
        return Result.success(list);
    }

    // 管理端分页查询banner
    @GetMapping("/admin/banners/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Banner>> getBannerPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status
    ) {
        Page<Banner> page = new Page<>(current, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Banner::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSort);
        return Result.success(bannerService.page(page, wrapper));
    }

    // 管理端新增banner
    @PostMapping("/admin/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success(banner);
    }

    // 管理端修改banner
    @PutMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.updateById(banner);
        return Result.success(banner);
    }

    // 管理端删除banner
    @DeleteMapping("/admin/banners/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }
} 