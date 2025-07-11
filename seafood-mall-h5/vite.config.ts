/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import postCssPxToViewport from 'postcss-px-to-viewport-8-plugin'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    host: '::',
    port: 5174, // Frontend runs on port 5174
    proxy: {
      '/api': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:9000',
        changeOrigin: true,
      }
    },
    allowedHosts: ['h5-mall.demo.icodestar.net','admin-mall.demo.icodestar.net']
  },
  css: {
    postcss: {
      plugins: [
        postCssPxToViewport({
          unitToConvert: 'px',
          viewportWidth: 375, // 设计稿的视口宽度，例如 iPhone 6/7/8 是 375
          unitPrecision: 5, // 指定`px`转换为视口单位值的小数位数
          propList: ['*', '!font-size'], // 指定需要转换的 CSS 属性，*表示全部，!font-size表示不转换字体大小
          viewportUnit: 'vw', // 指定需要使用的视口单位
          fontViewportUnit: 'vw', // 字体使用的视口单位
          selectorBlackList: [], // 指定不转换为视口单位的类名
          minPixelValue: 1, // 小于或等于`1px`不转换为视口单位
          mediaQuery: false, // 允许在媒体查询中转换`px`
          replace: true, // 是否直接替换 `px`
          exclude: [], // 忽略某些文件
          landscape: false, // 是否处理横屏
          landscapeUnit: 'vw', // 横屏时使用的单位
          landscapeWidth: 568 // 横屏时使用的视口宽度
        })
      ]
    }
  }
}) 