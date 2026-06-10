<template>
  <el-card>
    <h2>我的发布</h2>
    <el-table :data="items" v-loading="loading">
      <el-table-column prop="title" label="名称" />
      <el-table-column prop="category" label="类别" width="100" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="edit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row.id)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="编辑物品" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="editForm.price" :min="0.01" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { itemApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const items = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editForm = reactive({ id: null, title: '', price: null, description: '', category: '' })

async function load() {
  loading.value = true
  try {
    const res = await itemApi.mine({ page: 1, size: 50 })
    items.value = res.data.records
  } finally {
    loading.value = false
  }
}

function edit(row) {
  Object.assign(editForm, row)
  dialogVisible.value = true
}

async function saveEdit() {
  await itemApi.update(editForm.id, editForm)
  ElMessage.success('已更新')
  dialogVisible.value = false
  load()
}

async function remove(id) {
  await ElMessageBox.confirm('确认下架该物品？')
  await itemApi.remove(id)
  ElMessage.success('已下架')
  load()
}

onMounted(load)
</script>

<style scoped>
h2 { margin-bottom: 16px; }
</style>
