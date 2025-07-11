<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-right">
            <el-input
              v-model="searchQuery"
              placeholder="搜索订单号"
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
            />
            <el-select
              v-model="orderStatus"
              placeholder="订单状态"
              style="width: 120px; margin-right: 16px"
              @change="handleSearch"
            >
              <el-option label="全部" value="" />
              <el-option label="待付款" :value="0" />
              <el-option label="待发货" :value="1" />
              <el-option label="已发货" :value="2" />
              <el-option label="已完成" :value="3" />
              <el-option label="已取消" :value="4" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="orders"
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="下单时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="warning" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 0"
              type="danger" 
              link 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="订单详情"
      width="800px"
      :before-close="handleCloseViewDialog"
    >
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentOrder.username || '未知用户' }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createdTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentOrder.updatedTime || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPaymentTypeText(currentOrder.paymentType) }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">{{ getPaymentStatusText(currentOrder.paymentStatus) }}</el-descriptions-item>
          <el-descriptions-item label="收货人信息" :span="2">
            <div v-if="currentOrder.consigneeName || currentOrder.consigneePhone">
              <div>收货人：{{ currentOrder.consigneeName || '暂无' }}</div>
              <div>联系电话：{{ currentOrder.consigneePhone || '暂无' }}</div>
            </div>
            <div v-else>暂无收货人信息</div>
          </el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.addressDetail || '暂无地址信息' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无备注' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 订单商品列表 -->
        <div v-if="currentOrder.items && currentOrder.items.length > 0" class="order-items">
          <h4>订单商品</h4>
          <el-table :data="currentOrder.items" style="width: 100%">
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">
                ¥{{ row.price?.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="规格" width="80" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="小计" width="100">
              <template #default="{ row }">
                ¥{{ row.totalAmount?.toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button 
            v-if="currentOrder && currentOrder.status === 1"
            type="success" 
            @click="handleShipFromDialog"
          >
            发货
          </el-button>
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 订单编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑订单"
      width="600px"
      :before-close="handleCloseEditDialog"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="editForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="订单金额" prop="totalAmount">
          <el-input-number
            v-model="editForm.totalAmount"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="已发货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentType">
          <el-select v-model="editForm.paymentType" style="width: 100%">
            <el-option label="在线支付" :value="1" />
            <el-option label="货到付款" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态" prop="paymentStatus">
          <el-select v-model="editForm.paymentStatus" style="width: 100%">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import request from '../utils/request'

// 定义接口
interface OrderItem {
  id?: number
  orderId?: number
  productId: number
  productName: string
  productImage?: string
  price: number
  quantity: number
  totalAmount: number
  unit?: string
  createdTime?: string
  updatedTime?: string
  deleted?: boolean
}

interface Order {
  id?: number
  orderNo: string
  userId: number
  username?: string
  totalAmount: number
  status: number
  paymentType?: number
  paymentStatus?: number
  addressId?: number
  addressDetail?: string
  consigneeName?: string
  consigneePhone?: string
  remark?: string
  createdTime: string
  updatedTime?: string
  items?: OrderItem[]
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

interface Result<T> {
  code: number
  message: string
  data: T
}

const loading = ref(false)
const searchQuery = ref('')
const orderStatus = ref<number | ''>('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orders = ref<Order[]>([])

// 查看功能相关
const viewDialogVisible = ref(false)
const currentOrder = ref<Order | null>(null)

// 编辑功能相关
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = ref<Order>({
  orderNo: '',
  userId: 0,
  username: '',
  totalAmount: 0,
  status: 0,
  paymentType: 1,
  paymentStatus: 0,
  remark: '',
  createdTime: ''
})

// 表单验证规则
const editRules: FormRules = {
  totalAmount: [
    { required: true, message: '请输入订单金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于等于0', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择订单状态', trigger: 'change' }
  ],
  paymentType: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ],
  paymentStatus: [
    { required: true, message: '请选择支付状态', trigger: 'change' }
  ]
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      status: orderStatus.value !== '' ? orderStatus.value : undefined,
      orderNo: searchQuery.value || undefined
    }
    const response = await request<Result<PageResult<Order>>>({
      url: '/api/admin/orders/page',
      method: 'get',
      params
    })
    if (response && response.data) {
      orders.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      orders.value = []
      total.value = 0
    }
  } catch (error: any) {
    console.error('Failed to load orders:', error)
    ElMessage.error(error.response?.data?.message || '获取订单列表失败')
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: number) => {
  const statusMap: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: number) => {
  const statusMap: Record<number, string> = {
    0: '待付款',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

const getPaymentTypeText = (paymentType?: number) => {
  const paymentTypeMap: Record<number, string> = {
    1: '在线支付',
    3: '货到付款'
  }
  return paymentTypeMap[paymentType || 0] || '未知'
}

const getPaymentStatusText = (paymentStatus?: number) => {
  const paymentStatusMap: Record<number, string> = {
    0: '未支付',
    1: '已支付'
  }
  return paymentStatusMap[paymentStatus || 0] || '未知'
}

const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadOrders()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadOrders()
}

// 查看订单详情
const handleView = async (row: Order) => {
  try {
    const response = await request<Result<Order>>({
      url: `/api/admin/orders/${row.id}`,
      method: 'get'
    })
    if (response && response.data) {
      currentOrder.value = response.data
      viewDialogVisible.value = true
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error: any) {
    console.error('Failed to get order detail:', error)
    ElMessage.error(error.response?.data?.message || '获取订单详情失败')
  }
}

// 编辑订单
const handleEdit = async (row: Order) => {
  try {
    const response = await request<Result<Order>>({
      url: `/api/admin/orders/${row.id}`,
      method: 'get'
    })
    if (response && response.data) {
      editForm.value = { ...response.data }
      editDialogVisible.value = true
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error: any) {
    console.error('Failed to get order detail for edit:', error)
    ElMessage.error(error.response?.data?.message || '获取订单详情失败')
  }
}

// 保存编辑
const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      try {
        const response = await request<Result<Order>>({
          url: `/api/admin/orders/${editForm.value.id}`,
          method: 'put',
          data: editForm.value
        })
        if (response && response.data) {
          ElMessage.success('订单更新成功')
          editDialogVisible.value = false
          loadOrders() // 刷新列表
        } else {
          ElMessage.error('订单更新失败')
        }
      } catch (error: any) {
        console.error('Failed to update order:', error)
        ElMessage.error(error.response?.data?.message || '订单更新失败')
      }
    } else {
      console.log('Validation failed:', fields)
    }
  })
}

// 关闭查看对话框
const handleCloseViewDialog = () => {
  viewDialogVisible.value = false
  currentOrder.value = null
}

// 关闭编辑对话框
const handleCloseEditDialog = () => {
  editDialogVisible.value = false
  editForm.value = {
    orderNo: '',
    userId: 0,
    username: '',
    totalAmount: 0,
    status: 0,
    paymentType: 1,
    paymentStatus: 0,
    remark: '',
    createdTime: ''
  }
  if (editFormRef.value) {
    editFormRef.value.resetFields()
  }
}

// 从对话框中发货
const handleShipFromDialog = async () => {
  if (!currentOrder.value) return
  
  try {
    await request({
      url: `/api/admin/orders/${currentOrder.value.id}/status`,
      method: 'put',
      params: { status: 2 }
    })
    ElMessage.success('发货成功')
    viewDialogVisible.value = false // 关闭对话框
    loadOrders() // 刷新列表
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '发货失败')
  }
}

const handleCancel = async (row: Order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单"${row.orderNo}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await request({
      url: `/api/admin/orders/${row.id}/cancel`,
      method: 'put'
    })
    ElMessage.success('取消成功')
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '取消失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 订单详情样式 */
.order-detail {
  padding: 20px 0;
}

.order-items {
  margin-top: 20px;
}

.order-items h4 {
  margin-bottom: 16px;
  color: #303133;
  font-weight: 600;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input-number .el-input__wrapper) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

/* 描述列表样式 */
:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

/* 表格样式优化 */
:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 500;
}

:deep(.el-table td) {
  padding: 12px 0;
}

/* 标签样式 */
:deep(.el-tag) {
  font-weight: 500;
}

/* 按钮样式优化 */
:deep(.el-button--link) {
  padding: 4px 8px;
  font-size: 14px;
}

:deep(.el-button--link:hover) {
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style> 