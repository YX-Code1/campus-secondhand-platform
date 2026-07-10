<template>
  <el-card>
    <h2>我的交易</h2>
    <el-table :data="trades" v-loading="loading">
      <el-table-column prop="tradeNo" label="交易编号" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row.tradeNo)">{{ row.tradeNo }}</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="itemTitle" label="物品">
        <template #default="{ row }">{{ row.itemTitle || '未知物品' }}</template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.status === 'COMPLETED'" size="small" disabled>已完成</el-button>
          <el-button v-else-if="row.status === 'CANCELLED'" size="small" disabled>已取消</el-button>
          <template v-else>
            <el-button v-if="row.status === 'PENDING'" size="small" @click="updateStatus(row.id, 'IN_PROGRESS')">进行中</el-button>
            <el-button size="small" type="success" @click="updateStatus(row.id, 'COMPLETED')">完成</el-button>
            <el-button size="small" type="danger" @click="updateStatus(row.id, 'CANCELLED')">取消</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="detailVisible" title="交易详情" width="500px">
    <el-descriptions :column="2" v-if="detail">
      <el-descriptions-item label="交易编号">{{ detail.tradeNo }}</el-descriptions-item>
      <el-descriptions-item label="物品">{{ detail.itemTitle || '未知物品' }}</el-descriptions-item>
      <el-descriptions-item label="金额">¥{{ detail.amount }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="买家">{{ detail.buyerName || '未知' }}</el-descriptions-item>
      <el-descriptions-item label="卖家">{{ detail.sellerName || '未知' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { tradeApi } from '../api'
import { ElMessage } from 'element-plus'

const trades = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref(null)

const labels = { PENDING: '待处理', IN_PROGRESS: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }
const types = { PENDING: 'warning', IN_PROGRESS: '', COMPLETED: 'success', CANCELLED: 'info' }
const statusLabel = s => labels[s] || s
const statusType = s => types[s] || ''

async function load() {
  loading.value = true
  try {
    const res = await tradeApi.mine()
    trades.value = res.data
  } finally {
    loading.value = false
  }
}

async function updateStatus(id, status) {
  await tradeApi.updateStatus(id, status)
  ElMessage.success('状态已更新')
  load()
}

async function showDetail(tradeNo) {
  try {
    const res = await tradeApi.detail(tradeNo)
    detail.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败：' + (error.message || '未知错误'))
  }
}

onMounted(load)
</script>

<style scoped>
h2 { margin-bottom: 16px; }
</style>
