<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总销售额</span>
              <el-tag type="success">月</el-tag>
            </div>
          </template>
          <div class="card-value">¥ {{ totalSales }}</div>
          <div class="card-footer">
            <span>日同比 {{ salesComparison }}</span>
            <el-icon color="#67C23A"><ArrowUp /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>订单数量</span>
              <el-tag type="warning">月</el-tag>
            </div>
          </template>
          <div class="card-value">{{ orderCount }}</div>
          <div class="card-footer">
            <span>日同比 {{ orderComparison }}</span>
            <el-icon color="#67C23A"><ArrowUp /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>商品数量</span>
              <el-tag type="info">总计</el-tag>
            </div>
          </template>
          <div class="card-value">{{ productCount }}</div>
          <div class="card-footer">
            <span>本月新增 {{ newProducts }}</span>
            <el-icon color="#67C23A"><ArrowUp /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>用户数量</span>
              <el-tag type="danger">总计</el-tag>
            </div>
          </template>
          <div class="card-value">{{ userCount }}</div>
          <div class="card-footer">
            <span>本月新增 {{ newUsers }}</span>
            <el-icon color="#67C23A"><ArrowUp /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="id" label="订单号" width="180" />
            <el-table-column prop="customer" label="客户" width="120" />
            <el-table-column prop="amount" label="金额" width="120" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热销商品</span>
            </div>
          </template>
          <el-table :data="hotProducts" style="width: 100%">
            <el-table-column prop="name" label="商品名称" width="180" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="sales" label="销量" width="120" />
            <el-table-column prop="stock" label="库存" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ArrowUp } from '@element-plus/icons-vue'
import { getDashboardData } from '@/api/dashboard'

const totalSales = ref('0.00')
const salesComparison = ref('0%')
const orderCount = ref(0)
const orderComparison = ref('0%')
const productCount = ref(0)
const newProducts = ref(0)
const userCount = ref(0)
const newUsers = ref(0)

const recentOrders = ref<any[]>([])
const hotProducts = ref<any[]>([])

// 订单状态映射（支持数字和字符串）
const getStatusType = (status: number | string) => {
  const map: Record<string | number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'info',
    '待付款': 'warning',
    '待发货': 'primary',
    '已发货': 'success',
    '已完成': 'success',
    '已取消': 'info',
  };
  return map[status] || 'info';
};
const getStatusText = (status: number | string) => {
  const map: Record<string | number, string> = {
    0: '待付款',
    1: '待发货',
    2: '已发货',
    3: '已完成',
    4: '已取消',
    '待付款': '待付款',
    '待发货': '待发货',
    '已发货': '已发货',
    '已完成': '已完成',
    '已取消': '已取消',
  };
  return map[status] || '未知';
};

const fetchDashboardData = async () => {
  try {
    const response = await getDashboardData()
    const data = response.data

    totalSales.value = data.stats.totalSalesAmount ? data.stats.totalSalesAmount.toLocaleString() : '0.00'
    orderCount.value = data.stats.totalOrderCount || 0
    productCount.value = data.stats.totalProductCount || 0
    newProducts.value = data.stats.monthlyNewProducts || 0
    userCount.value = data.stats.totalUserCount || 0
    newUsers.value = data.stats.monthlyNewUsers || 0

    recentOrders.value = data.recentOrders || []
    hotProducts.value = data.hotProducts || []

  } catch (error) {
    console.error('Error fetching dashboard data:', error)
    // Handle error, maybe show a message to the user
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.data-card {
  height: 180px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin: 20px 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
}

.mt-20 {
  margin-top: 20px;
}
</style> 