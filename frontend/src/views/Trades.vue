<template>
  <el-card>
    <h2>我的交易</h2>
    <el-table :data="trades" v-loading="loading">
      <el-table-column prop="tradeNo" label="交易编号" width="200" />
      <el-table-column prop="itemTitle" label="物品" />
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { tradeApi } from '../api'
import { ElMessage } from 'element-plus'

const trades = ref([])
const loading = ref(false)

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

onMounted(load)
</script>

<style scoped>
h2 { margin-bottom: 16px; }
</style>
