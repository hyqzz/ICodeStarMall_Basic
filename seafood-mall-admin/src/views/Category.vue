<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="category-container">
    <h2>商品分类管理</h2>

    <div class="header-actions">
      <el-button type="primary" @click="handleAdd">添加分类</el-button>
    </div>

    <el-table :data="categories" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="分类名称"></el-table-column>
      <el-table-column prop="parentId" label="父分类ID" width="120"></el-table-column>
      <el-table-column prop="level" label="层级" width="80"></el-table-column>
      <el-table-column prop="sort" label="排序" width="80"></el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
      <el-table-column prop="updatedTime" label="更新时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-footer">
      <el-pagination
        v-model:currentPage="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '添加分类'"
      width="500px"
    >
      <el-form :model="currentCategory" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="currentCategory.name"></el-input>
        </el-form-item>
        <el-form-item label="父分类ID">
          <el-input v-model.number="currentCategory.parentId" type="number"></el-input>
        </el-form-item>
        <el-form-item label="层级">
          <el-input v-model.number="currentCategory.level" type="number"></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model.number="currentCategory.sort" type="number"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="currentCategory.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getAllCategories, CategoryItem, createCategory, updateCategory, deleteCategory, getCategoryPage } from '@/api/category';

const categories = ref<CategoryItem[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentCategory = ref<CategoryItem>({
  id: undefined,
  name: '',
  parentId: null,
  level: 1,
  sort: 0,
  status: 1,
  createdTime: '',
  updatedTime: '',
});

const fetchCategories = async () => {
  console.log('fetchCategories started');
  try {
    const params = { current: currentPage.value, size: pageSize.value };
    console.log('Calling getCategoryPage with params:', params);
    const response = await getCategoryPage(params);
    console.log('Raw getCategoryPage API response:', response);

    if (response && response.data && response.data.records) {
      categories.value = response.data.records;
      total.value = response.data.total;
    } else {
      categories.value = [];
      total.value = 0;
      console.warn('getCategoryPage did not return expected data structure or empty.', response);
    }
  } catch (error) {
    ElMessage.error('获取分类列表失败');
    console.error('Error fetching categories:', error);
    categories.value = [];
    total.value = 0;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  currentCategory.value = {
    id: undefined,
    name: '',
    parentId: null,
    level: 1,
    sort: 0,
    status: 1,
    createdTime: '',
    updatedTime: '',
  };
  dialogVisible.value = true;
};

const handleEdit = (row: CategoryItem) => {
  isEdit.value = true;
  currentCategory.value = { ...row };
  dialogVisible.value = true;
};

const handleDelete = async (id: number) => {
  ElMessageBox.confirm('确定删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteCategory(id);
      ElMessage.success('删除成功');
      fetchCategories();
    } catch (error) {
      ElMessage.error('删除失败');
      console.error('Error deleting category:', error);
    }
  }).catch(() => {
    // 用户取消删除
  });
};

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await updateCategory(currentCategory.value.id!, currentCategory.value);
      ElMessage.success('编辑成功');
    } else {
      await createCategory(currentCategory.value);
      ElMessage.success('添加成功');
    }
    dialogVisible.value = false;
    fetchCategories();
  } catch (error) {
    ElMessage.error(isEdit.value ? '编辑失败' : '添加失败');
    console.error('Error submitting form:', error);
  }
};

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchCategories();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchCategories();
};

onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.header-actions {
  margin-bottom: 20px;
}

.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 