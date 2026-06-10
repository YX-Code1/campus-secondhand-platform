<template>
  <el-card v-loading="loading" class="detail">
    <el-row :gutter="24">
      <el-col :span="10">
        <el-image :src="item.imageUrl" fit="contain" class="main-img">
          <template #error><div class="placeholder">暂无图片</div></template>
        </el-image>
      </el-col>
      <el-col :span="14">
        <h2>{{ item.title }}</h2>
        <p class="price">¥{{ item.price }}</p>
        <p>
          <el-tag>{{ item.category }}</el-tag>
          <el-tag type="info">{{ statusText }}</el-tag>
          <el-tag v-if="item.mine" type="warning">我的发布</el-tag>
        </p>
        <p class="desc">{{ item.description || '暂无描述' }}</p>
        <p class="time">发布时间：{{ item.createTime }}</p>

        <el-card class="seller-card" shadow="never">
          <div class="seller-row">
            <el-avatar :size="48">{{ sellerInitial }}</el-avatar>
            <div class="seller-info">
              <div class="seller-label">发布者</div>
              <div class="seller-name">{{ item.sellerName || '未知用户' }}</div>
              <div class="seller-id">用户 ID：{{ item.sellerId }}</div>
            </div>
          </div>
          <div class="seller-actions" v-if="!isOwnItem">
            <el-button type="primary" @click="goChat">
              <el-icon><ChatDotRound /></el-icon>
              私聊卖家
            </el-button>
          </div>
          <el-alert v-else type="info" :closable="false" show-icon title="这是您发布的物品" />
        </el-card>

        <el-button
          v-if="canBuy"
          type="primary"
          size="large"
          class="buy-btn"
          :loading="buying"
          @click="buy"
        >立即购买</el-button>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { itemApi, tradeApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {ChatDotRound} from "@element-plus/icons-vue";
const route = useRoute()
const router = useRouter()
const store = useUserStore()
const item = ref({})
const loading = ref(false)
const buying = ref(false)

const statusText = computed(() => ({
  ON_SALE: '在售', SOLD: '已售出', OFF_SHELF: '已下架', IN_TRADE: '交易中', AUDIT_REJECT: '审核未通过'
}[item.value.status] || item.value.status))

const isOwnItem = computed(() => item.value.mine || item.value.sellerId === store.user?.id)

const canBuy = computed(() =>
  store.isLogin &&
  item.value.status === 'ON_SALE' &&
  item.value.auditStatus === 'APPROVED' &&
  !isOwnItem.value
)

const sellerInitial = computed(() => (item.value.sellerName || '?').charAt(0).toUpperCase())

onMounted(async () => {
  loading.value = true
  try {
    const res = await itemApi.detail(route.params.id)
    item.value = res.data
  } finally {
    loading.value = false
  }
})

function goChat() {
  if (!store.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (isOwnItem.value) return
  router.push({
    path: '/messages',
    //带参数跳转
    query: { peerId: item.value.sellerId, name: item.value.sellerName }
  })
}

async function buy() {
  await ElMessageBox.confirm('确认发起购买请求？', '提示')
  buying.value = true
  try {
    const res = await tradeApi.create(item.value.id)
    ElMessage.success(`交易已创建：${res.data.tradeNo}`)
    router.push('/trades')
  } finally {
    buying.value = false
  }
}
</script>

<style scoped>
.detail { max-width: 1000px; margin: 0 auto; }
.main-img { width: 100%; height: 360px; background: #f5f7fa; }
.placeholder { height: 360px; display: flex; align-items: center; justify-content: center; color: #909399; }
.price { font-size: 28px; color: #f56c6c; font-weight: bold; margin: 12px 0; }
.desc { color: #606266; line-height: 1.8; margin: 16px 0; }
.time { color: #909399; font-size: 14px; margin: 4px 0 16px; }
.seller-card { margin: 16px 0; background: #fafafa; }
.seller-row { display: flex; gap: 16px; align-items: center; }
.seller-label { font-size: 12px; color: #909399; }
.seller-name { font-size: 18px; font-weight: 600; color: #303133; margin: 4px 0; }
.seller-id { font-size: 12px; color: #c0c4cc; }
.seller-actions { margin-top: 16px; }
.buy-btn { margin-top: 12px; }
</style>
