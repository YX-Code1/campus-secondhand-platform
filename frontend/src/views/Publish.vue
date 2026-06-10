<template>
  <el-card class="page-card">
    <h2>发布二手物品</h2>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="名称" prop="title"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="类别" prop="category">
        <el-select v-model="form.category" style="width:100%">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price" :min="0.01" :precision="2" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="图片">
        <el-upload :show-file-list="false" :http-request="uploadImage" accept="image/*">
          <el-image v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:120px" fit="cover" />
          <el-button v-else type="primary">上传图片</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">发布</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { itemApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const categories = ['书籍', '数码', '生活用品', '服饰', '运动', '其他']
const formRef = ref()
const loading = ref(false)
const form = reactive({ title: '', category: '', price: null, description: '', imageUrl: '' })
const rules = {
  title: [{ required: true, message: '请输入名称' }],
  category: [{ required: true, message: '请选择类别' }],
  price: [{ required: true, message: '请输入价格' }]
}

async function uploadImage({ file }) {
  const res = await itemApi.upload(file)
  form.imageUrl = res.data
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    //用的是item接口
    await itemApi.create(form)
    ElMessage.success('发布成功')
    router.push('/my-items')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-card { max-width: 640px; margin: 0 auto; }
.page-card h2 { margin-bottom: 24px; }
</style>
