<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="products-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <div class="header-controls">
            <el-input
              v-model="searchQuery"
              placeholder="搜索商品名称"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            />
            <el-select
              v-model="selectedCategoryId"
              placeholder="选择分类"
              clearable
              @change="handleCategoryChange"
              style="width: 150px; margin-right: 16px"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
            <el-button type="primary" @click="handleAdd">添加商品</el-button>
          </div>
        </div>
      </template>

      <el-table :data="products" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" width="180" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
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

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑商品' : '添加商品'"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="newProductForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="newProductForm.name"></el-input>
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select
            v-model="newProductForm.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="newProductForm.price"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="newProductForm.stock"
            :min="0"
            :step="1"
            style="width: 100%"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="newProductForm.unit"></el-input>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            class="avatar-uploader"
            action="/api/admin/files/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <img v-if="newProductForm.image" :src="getImageUrl(newProductForm.image)" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <div style="border: 1px solid #ccc">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="newProductForm.description"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleEditorCreated"
            />
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="newProductForm.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm(formRef)">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, shallowRef, onBeforeUnmount, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus'; // 导入 FormInstance 和 FormRules 类型
import { getProductsPage, deleteProduct, createProduct, updateProduct, getProductById } from '@/api/product'; // 假设有此API
import { getAllCategories } from '@/api/category'; // 导入分类API
import { Plus } from '@element-plus/icons-vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import '@wangeditor/editor/dist/css/style.css';
import { useAdminStore } from '@/stores/admin';
import type { ProductItem } from '@/api/product';
import type { CategoryItem } from '@/api/category';

const searchQuery = ref('');
const selectedCategoryId = ref<number | null>(null);
const categories = ref<CategoryItem[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 添加/编辑对话框相关
const dialogVisible = ref(false);
const isEditMode = ref(false); // 是否处于编辑模式
const newProductForm = ref<ProductItem>({
  categoryId: null, // 初始化为null，表示未选择
  name: '',
  price: 0,
  stock: 0,
  unit: '',
  image: '',
  description: '',
  status: 1, // 默认上架
});
const formRef = ref<FormInstance>();

// 表单验证规则
const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', message: '价格必须是数字' }
  ],
  stock: [
    { required: true, message: '请输入商品库存', trigger: 'blur' },
    { type: 'number', message: '库存必须是数字' }
  ],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
});

const adminStore = useAdminStore();
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${adminStore.token}`
}));

// 编辑器实例
const editorRef = shallowRef();

// 编辑器配置
const toolbarConfig = {
  excludeKeys: []
};

const editorConfig = computed(() => ({
  placeholder: '请输入商品描述...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/admin/files/upload',
      headers: uploadHeaders.value,
      fieldName: 'file',
      maxFileSize: 10 * 1024 * 1024,
      maxNumberOfFiles: 10,
      allowedFileTypes: ['image/*'],
      meta: {},
      customInsert(res: any, insertFn: any) {
        if (res.code === 200) {
          // 只插入相对路径，不拼接域名
          insertFn(res.data, '', res.data);
        } else {
          ElMessage.error('图片上传失败');
        }
      },
    }
  }
}));

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

const handleEditorCreated = (editor: any) => {
  editorRef.value = editor;
};

// 加载商品列表
const loadProducts = async () => {
  loading.value = true;
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      name: searchQuery.value || undefined,
      categoryId: selectedCategoryId.value || undefined,
      status: 1, // 默认只显示上架商品
    };
    const response = await getProductsPage(params);
    if (response && response.data) {
      // 对商品数据进行处理，添加 categoryName
      products.value = response.data.records.map((product: ProductItem) => {
        const category = categories.value.find(cat => cat.id === product.categoryId);
        return {
          ...product,
          categoryName: category ? category.name : '未知分类'
        };
      }) || [];
      total.value = response.data.total || 0;
    } else {
      products.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('Failed to load products:', error);
    ElMessage.error('加载商品失败');
    products.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getAllCategories();
    if (response && response.data && Array.isArray(response.data)) {
      categories.value = response.data;
    } else {
      categories.value = [];
    }
  } catch (error) {
    console.error('Failed to load categories:', error);
    ElMessage.error('加载分类失败');
    categories.value = [];
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadProducts();
};

// 处理分类选择变化
const handleCategoryChange = () => {
  currentPage.value = 1;
  loadProducts();
};

// 处理每页显示数量变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  loadProducts();
};

// 处理当前页码变化
const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
  loadProducts();
};

// 打开添加商品对话框
const handleAdd = () => {
  isEditMode.value = false;
  dialogVisible.value = true;
  resetForm(); // 确保打开时表单是空的
};

// 提交表单 (添加/编辑)
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        if (isEditMode.value) {
          // 编辑商品
          const productData = { ...newProductForm.value };
          const response = await updateProduct(productData.id as number, productData as ProductItem);
          if (response.code === 200) {
            ElMessage.success('编辑商品成功');
            dialogVisible.value = false;
            loadProducts(); // 刷新列表
          } else {
            ElMessage.error(response.message || '编辑商品失败');
          }
        } else {
          // 添加商品
          const productData = { ...newProductForm.value };
          // Element Plus 的 el-input-number 返回的是 number 类型，直接发送即可
          const response = await createProduct(productData as ProductItem); 
          if (response.code === 200) {
            ElMessage.success('添加商品成功');
            dialogVisible.value = false;
            loadProducts(); // 刷新列表
          } else {
            ElMessage.error(response.message || '添加商品失败');
          }
        }
      } catch (error) {
        console.error('商品操作失败:', error);
        ElMessage.error('商品操作失败');
      }
    } else {
      console.log('error submit!', fields);
    }
  });
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
    // 只在添加模式下重置表单数据
    if (!isEditMode.value) {
      newProductForm.value = {
        categoryId: null,
        name: '',
        price: 0,
        stock: 0,
        unit: '',
        image: '',
        description: '',
        status: 1,
      };
    }
  }
};

const handleEdit = async (row: ProductItem) => {
  try {
    // 先获取最新的商品详情
    const response = await getProductById(row.id as number);
    if (response.code === 200 && response.data) {
      isEditMode.value = true;
      newProductForm.value = { ...response.data }; // 使用最新的商品数据填充表单
      dialogVisible.value = true;
    } else {
      ElMessage.error('获取商品详情失败');
    }
  } catch (error) {
    console.error('获取商品详情失败:', error);
    ElMessage.error('获取商品详情失败');
  }
};

const handleDelete = (row: ProductItem) => {
  ElMessageBox.confirm(
    `确定要删除商品 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      // 假设后端删除接口返回Result<Void>
      await deleteProduct(row.id as number); // 确保id是number类型
      ElMessage.success('删除成功');
      loadProducts(); // 刷新列表
    } catch (error) {
      console.error('删除商品失败:', error);
      ElMessage.error('删除商品失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const getImageUrl = (path: string) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  return `${window.location.origin}${path}`;
};

// 处理图片上传成功
const handleUploadSuccess = (response: any, uploadFile: any) => {
  console.log('图片上传成功响应:', response);
  if (response.code === 200 && response.data) {
    // 只保存路径
    newProductForm.value.image = response.data;
    ElMessage.success('图片上传成功！');
  } else {
    ElMessage.error('图片上传失败: ' + (response.message || '未知错误'));
  }
};

// 图片上传前校验
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

const handleUploadError = (error: any) => {
  ElMessage.error('图片上传失败');
};

onMounted(() => {
  loadCategories(); // 先加载分类
  loadProducts();   // 再加载商品
});

watch(searchQuery, () => {
  // 当搜索框内容变化时，不立即触发搜索，等待用户点击或回车
  // 如果需要实时搜索，可以将 handleSearch 直接放在这里
});
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-controls {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 