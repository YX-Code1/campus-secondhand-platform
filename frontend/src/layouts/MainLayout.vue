<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">
        <el-icon><Shop /></el-icon>
        <span>校园二手交易</span>
      </div>
      <el-menu mode="horizontal" :ellipsis="false" router :default-active="activeMenu" class="nav">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/publish">发布物品</el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/my-items">我的发布</el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/trades">我的交易</el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/admin">后台管理</el-menu-item>
      </el-menu>
      <div class="user-area">
        <template v-if="userStore.isLogin">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="msg-badge">
            <el-button circle @click="$router.push('/messages')">
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
          </el-badge>
          <el-dropdown trigger="click" @visible-change="onDropdown">
            <div class="user-trigger">
              <el-avatar :size="36" class="avatar">{{ avatarText }}</el-avatar>
              <div class="user-meta">
                <span class="user-name">{{ displayName }}</span>
                <span class="user-role">{{ roleLabel }}</span>
              </div>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown">
                <div class="dropdown-header">
                  <el-avatar :size="48">{{ avatarText }}</el-avatar>
                  <div>
                    <div class="dh-name">{{ displayName }}</div>
                    <div class="dh-user">@{{ userStore.user?.username }}</div>
                  </div>
                </div>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/my-items')">
                  <el-icon><Goods /></el-icon>我的发布
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/trades')">
                  <el-icon><ShoppingCart /></el-icon>我的交易
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/messages')">
                  <el-icon><ChatDotRound /></el-icon>消息中心
                  <el-badge v-if="unreadCount" :value="unreadCount" class="menu-badge" />
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin')">
                  <el-icon><Setting /></el-icon>后台管理
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main><router-view /></el-main>
    <el-footer class="footer">DB2026-RA-V1.0 校园二手物品交易管理系统</el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '../stores/user'
import { useRoute, useRouter } from 'vue-router'
import { chatApi } from '../api'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const unreadCount = ref(0)
let timer = null

const activeMenu = computed(() => route.path)
const displayName = computed(() => userStore.user?.realName || userStore.user?.username || '')
const avatarText = computed(() => (displayName.value || '?').charAt(0).toUpperCase())
const roleLabel = computed(() => ({
  STUDENT: '学生',
  STAFF: '教职工',
  ADMIN: '管理员'
}[userStore.user?.role] || ''))

async function loadUnread() {
  if (!userStore.isLogin) return
  try {
    const res = await chatApi.unreadCount()
    unreadCount.value = res.data || 0
  } catch {
    unreadCount.value = 0
  }
}

function onDropdown(visible) {
  if (visible) loadUnread()
}

function logout() {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadUnread()
  timer = setInterval(loadUnread, 30000)
})

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.layout { min-height: 100vh; }
.header {
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
  padding: 0 24px;
  height: 64px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  cursor: pointer;
  margin-right: 24px;
  white-space: nowrap;
}
.nav { flex: 1; border-bottom: none; }
.user-area {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background .2s;
}
.user-trigger:hover { background: #f5f7fa; }
.avatar { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; font-weight: 600; }
.user-meta { display: flex; flex-direction: column; line-height: 1.3; text-align: left; }
.user-name { font-size: 14px; font-weight: 600; color: #303133; }
.user-role { font-size: 12px; color: #909399; }
.arrow { color: #c0c4cc; font-size: 12px; }
.msg-badge { margin-right: 4px; }
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 4px;
}
.dh-name { font-weight: 600; color: #303133; }
.dh-user { font-size: 12px; color: #909399; margin-top: 2px; }
.menu-badge { margin-left: 8px; }
.footer {
  text-align: center;
  color: #909399;
  font-size: 13px;
  background: #fff;
}
</style>
