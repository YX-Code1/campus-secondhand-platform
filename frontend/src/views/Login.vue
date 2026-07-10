<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>用户登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="4-20位字母数字下划线" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item class="submit-group">
          <el-button type="primary" :loading="loading" @click="submit">登录</el-button>
        </el-form-item>
        <el-form-item class="link-group">
          <span class="auth-link" @click="$router.push('/register')">去注册</span>
          <span class="auth-link-divider">|</span>
          <span class="auth-link" @click="$router.push('/')">返回首页</span>
        </el-form-item>
      </el-form>
      <el-alert type="info" :closable="false" show-icon>
        管理员账号：admin / Admin@123
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await authApi.login(form)
    store.setAuth(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    router.push('/')
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
.auth-card { width: 420px; }
.auth-card h2 { text-align: center; margin-bottom: 24px; color: #303133; }
.submit-group { text-align: center; margin-bottom: 0; }
.link-group { text-align: center; margin-bottom: 0; padding-top: 8px; }
.auth-link {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
}
.auth-link:hover {
  color: #409eff;
  text-decoration: underline;
}
.auth-link-divider {
  color: #dcdfe6;
  margin: 0 12px;
}
</style>
