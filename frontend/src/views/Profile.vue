<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <el-card class="profile-side">
          <div class="profile-hero">
            <el-avatar :size="80" class="hero-avatar">{{ avatarText }}</el-avatar>
            <h2>{{ form.realName || form.username }}</h2>
            <p class="sub">@{{ form.username }}</p>
            <el-tag>{{ roleLabel }}</el-tag>
          </div>
          <el-divider />
          <div class="quick-links">
            <el-button text @click="$router.push('/my-items')">我的发布</el-button>
            <el-button text @click="$router.push('/trades')">我的交易</el-button>
            <el-button text @click="$router.push('/messages')">消息中心</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="16">
        <el-card>
          <h3>编辑资料</h3>
          <el-form :model="form" label-width="80px" class="profile-form">
            <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
            <el-form-item label="姓名"><el-input v-model="form.realName" placeholder="真实姓名" /></el-form-item>
            <el-form-item label="手机"><el-input v-model="form.phone" placeholder="手机号" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="邮箱" /></el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" @click="save">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const store = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', realName: '', phone: '', email: '', role: '' })
const roleLabel = computed(() => ({ STUDENT: '学生', STAFF: '教职工', ADMIN: '管理员' }[form.role] || form.role))
const avatarText = computed(() => (form.realName || form.username || '?').charAt(0).toUpperCase())


onMounted(async () => {
  //加载个人信息
  const res = await userApi.me()
  Object.assign(form, res.data)
})

async function save() {
  loading.value = true
  try {
    const res = await userApi.updateMe({ realName: form.realName, phone: form.phone, email: form.email })
    store.setAuth(store.token, res.data)
    ElMessage.success('保存成功')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.profile-page { max-width: 960px; margin: 0 auto; }
.profile-side { text-align: center; }
.profile-hero { padding: 16px 0; }
.hero-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-size: 32px;
  font-weight: 600;
}
.profile-hero h2 { margin: 12px 0 4px; }
.sub { color: #909399; font-size: 14px; margin-bottom: 12px; }
.quick-links { display: flex; flex-direction: column; gap: 4px; }
.profile-form { margin-top: 16px; }
h3 { margin-bottom: 20px; }
</style>
