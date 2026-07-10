<template>
  <div class="admin">
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6"><el-statistic title="交易总量" :value="stats.totalTrades" /></el-col>
      <el-col :span="6"><el-statistic title="已完成" :value="stats.completedTrades" /></el-col>
      <el-col :span="6"><el-statistic title="用户总数" :value="stats.totalUsers" /></el-col>
      <el-col :span="6"><el-statistic title="待审核物品" :value="stats.pendingAuditItems" /></el-col>
    </el-row>

    <el-card class="chart-card">
      <h3>热门物品 TOP5</h3>
      <div ref="chartRef" style="height:300px"></div>
    </el-card>

    <el-tabs v-model="tab">
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="role" label="角色" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">{{ row.status === 1 ? '正常' : '禁用' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.role !== 'ADMIN'" size="small" @click="toggleUser(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button v-if="row.role !== 'ADMIN'" size="small" type="danger" @click="delUser(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="userPage"
            v-model:page-size="userPageSize"
            :total="userTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleUserPageSizeChange"
            @current-change="handleUserPageChange"
          />
        </div>
      </el-tab-pane>
      <el-tab-pane label="物品审核" name="audit">
        <el-table :data="auditItems">
          <el-table-column prop="title" label="名称" />
          <el-table-column prop="category" label="类别" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="audit(row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" @click="audit(row.id, 'REJECTED')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="数据导出" name="export">
        <el-button type="primary" @click="exportData('items')">导出物品 Excel</el-button>
        <el-button type="primary" @click="exportData('trades')">导出交易 Excel</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useUserStore()
const tab = ref('users')
const stats = reactive({ totalTrades: 0, completedTrades: 0, totalUsers: 0, pendingAuditItems: 0, hotItems: [] })
const users = ref([])
const auditItems = ref([])
const chartRef = ref()
const userPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

async function loadStats() {
  const res = await adminApi.stats()
  Object.assign(stats, res.data)
  await nextTick()
  if (chartRef.value && stats.hotItems?.length) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: {},
      xAxis: { type: 'category', data: stats.hotItems.slice(0, 5).map(i => i.title) },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: stats.hotItems.slice(0, 5).map(i => i.viewCount), itemStyle: { color: '#409eff' } }]
    })
  }
}

async function loadUsers() {
  const res = await adminApi.users({ page: userPage.value, size: userPageSize.value })
  users.value = res.data.records
  userTotal.value = res.data.total
}

function handleUserPageChange(val) {
  userPage.value = val
  loadUsers()
}

function handleUserPageSizeChange(val) {
  userPageSize.value = val
  userPage.value = 1
  loadUsers()
}

async function loadAudit() {
  const res = await adminApi.pendingAudit()
  auditItems.value = res.data
}

async function toggleUser(row) {
  await adminApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('已更新')
  loadUsers()
}

async function delUser(id) {
  await ElMessageBox.confirm('确认删除该用户？')
  await adminApi.deleteUser(id)
  ElMessage.success('已删除')
  loadUsers()
}

async function audit(id, status) {
  await adminApi.auditItem(id, status)
  ElMessage.success('审核完成')
  loadAudit()
  loadStats()
}

async function exportData(type) {
  const res = await fetch(`/api/admin/export/${type}`, {
    headers: { Authorization: `Bearer ${store.token}` }
  })
  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = type === 'items' ? '物品列表.xlsx' : '交易记录.xlsx'
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => {
  loadStats()
  loadUsers()
  loadAudit()
})
</script>

<style scoped>
.stats-row { margin-bottom: 20px; }
.chart-card { margin-bottom: 20px; }
.chart-card h3 { margin-bottom: 12px; }
.pagination { margin-top: 20px; text-align: right; }
</style>
