<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="教职工" value="STAFF" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">注册</el-button>
          <el-button link @click="$router.push('/login')">已有账号</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', realName: '', phone: '', role: 'STUDENT' })
const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '4-20位字母数字下划线' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '6-20位' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '需包含字母和数字' }
  ]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await authApi.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.auth-card { width: 460px; }
.auth-card h2 { text-align: center; margin-bottom: 24px; }
</style>
