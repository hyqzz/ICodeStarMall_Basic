<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="banners-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <div class="header-right">
            <el-input
              v-model="searchQuery"
              placeholder="搜索标题"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleAdd">添加轮播图</el-button>
          </div>
        </div>
      </template>

      <el-table :data="banners" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="image" label="图片" width="120">
          <template #default="{ row }">
            <el-image :src="getImageUrl(row.image)" style="width: 80px; height: 40px;" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="link" label="跳转链接" width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="val => handleStatusChange(row, val)" />
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑轮播图对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '添加轮播图'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="banner-uploader"
            action="/api/admin/files/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders"
          >
            <img v-if="form.image" :src="getImageUrl(form.image)" class="banner-img" />
            <el-icon v-else class="banner-uploader-icon"><plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.link" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';
import { useAdminStore } from '@/stores/admin';

interface Banner {
  id?: number;
  title: string;
  image: string;
  link?: string;
  sort: number;
  status: number;
  createdTime?: string;
  updatedTime?: string;
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
const banners = ref<Banner[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = ref<Banner>({ title: '', image: '', link: '', sort: 0, status: 1 });

const adminStore = useAdminStore();
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${adminStore.token}`
}));

const loadBanners = async () => {
  loading.value = true;
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      title: searchQuery.value || undefined,
    };
    const response = await request<Result<PageResult<Banner>>>(
      { url: '/api/admin/banners/page', method: 'get', params }
    );
    if (response && response.data) {
      banners.value = response.data.records || [];
      total.value = response.data.total || 0;
    } else {
      banners.value = [];
      total.value = 0;
    }
  } catch (error) {
    ElMessage.error('加载轮播图失败');
    banners.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadBanners();
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  loadBanners();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  loadBanners();
};

const handleAdd = () => {
  isEdit.value = false;
  form.value = { title: '', image: '', link: '', sort: 0, status: 1 };
  dialogVisible.value = true;
};

const handleEdit = (row: Banner) => {
  isEdit.value = true;
  form.value = { ...row };
  dialogVisible.value = true;
};

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await request({ url: `/api/admin/banners/${form.value.id}`, method: 'put', data: form.value });
      ElMessage.success('编辑成功');
    } else {
      await request({ url: '/api/admin/banners', method: 'post', data: form.value });
      ElMessage.success('添加成功');
    }
    dialogVisible.value = false;
    loadBanners();
  } catch (error) {
    ElMessage.error('操作失败');
  }
};

const handleDelete = (row: Banner) => {
  ElMessageBox.confirm(`确定要删除轮播图 "${row.title}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await request({ url: `/api/admin/banners/${row.id}`, method: 'delete' });
      ElMessage.success('删除成功');
      loadBanners();
    } catch (error) {
      ElMessage.error('删除失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const handleStatusChange = async (row: Banner, val: number) => {
  try {
    await request({ url: `/api/admin/banners/${row.id}`, method: 'put', data: { ...row, status: val } });
    ElMessage.success('状态已更新');
  } catch (error) {
    ElMessage.error('状态更新失败');
    row.status = val === 1 ? 0 : 1; // 回滚
  }
};

const getImageUrl = (path: string) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${window.location.origin}${path}`;
};

const handleUploadSuccess = (response: any, uploadFile: any) => {
  console.log('图片上传成功响应:', response);
  if (response.code === 200 && response.data) {
    // 只保存路径
    form.value.image = response.data;
    ElMessage.success('图片上传成功！');
  } else {
    ElMessage.error('图片上传失败: ' + (response.message || '未知错误'));
  }
};

const beforeUpload = (rawFile: any) => {
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return false;
  }
  if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('图片大小不能超过 10MB！');
    return false;
  }
  return true;
};

onMounted(() => {
  loadBanners();
});
</script>

<style scoped>
.banners-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-right {
  display: flex;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.banner-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 120px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.banner-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.banner-img {
  width: 120px;
  height: 60px;
  object-fit: cover;
  display: block;
}
</style> 