<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-right">
            <el-input
              v-model="searchQuery"
              placeholder="搜索用户名"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleAdd">添加用户</el-button>
          </div>
        </div>
      </template>

      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="level" label="用户等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getUserLevelTagType(row.level)">
              {{ getUserLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              type="danger" 
              link 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

// 定义接口
interface User {
  id?: number;
  username: string;
  realName?: string;
  phone?: string;
  email?: string;
  userType: number; // 0-普通用户，1-月结用户，2-储值卡用户
  status: number; // 0-禁用，1-启用
  level: number; // 0-普通用户，1-银牌用户，2-金牌用户
  createdTime: string;
  updatedTime: string;
}

interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

interface Result<T> {
  code: number;
  message: string;
  data: T;
}

const searchQuery = ref('');
const users = ref<User[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取用户等级文本
const getUserLevelText = (level: number) => {
  switch (level) {
    case 0: return '普通用户';
    case 1: return '银牌用户';
    case 2: return '金牌用户';
    default: return '未知';
  }
};

// 获取用户等级标签类型
const getUserLevelTagType = (level: number) => {
  switch (level) {
    case 0: return 'info';
    case 1: return ''; // default
    case 2: return 'success';
    default: return 'info';
  }
};

// 加载用户列表
const loadUsers = async () => {
  loading.value = true;
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      username: searchQuery.value || undefined,
    };
    const response = await request<Result<PageResult<User>>>({
      url: '/api/users/page',
      method: 'get',
      params
    });
    if (response && response.data) {
      users.value = response.data.records || [];
      total.value = response.data.total || 0;
    } else {
      users.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('Failed to load users:', error);
    ElMessage.error('加载用户失败');
    users.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadUsers();
};

// 处理每页显示数量变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  loadUsers();
};

// 处理当前页码变化
const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  loadUsers();
};

const handleAdd = () => {
  ElMessage.info('添加用户功能待开发...');
};

const handleEdit = (row: User) => {
  ElMessage.info(`编辑用户：${row.username} 功能待开发...`);
};

const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await request({
        url: `/api/users/${row.id}`,
        method: 'delete'
      });
      ElMessage.success('删除成功');
      loadUsers(); // 刷新列表
    } catch (error) {
      console.error('删除用户失败:', error);
      ElMessage.error('删除用户失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

onMounted(() => {
  loadUsers();
});

watch(searchQuery, () => {
  // 当搜索框内容变化时，不立即触发搜索，等待用户点击或回车
  // 如果需要实时搜索，可以将 handleSearch 直接放在这里
});
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px; /* 调整标题和内容的间距 */
}

.header-right {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end; /* 分页组件靠右对齐 */
}
</style> 