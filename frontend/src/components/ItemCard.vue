<template>
  <el-card class="item-card" shadow="hover" @click="$router.push(`/items/${item.id}`)">
    <el-image :src="imageUrl" fit="cover" class="cover">
      <template #error><div class="img-placeholder">暂无图片</div></template>
    </el-image>
    <el-tag v-if="item.mine" type="warning" size="small" class="mine-tag">我的</el-tag>
    <el-tag v-else-if="item.auditStatus === 'PENDING'" type="info" size="small" class="mine-tag">审核中</el-tag>
    <div class="info">
      <div class="title">{{ item.title }}</div>
      <div class="seller" v-if="item.sellerName">
        <el-icon><User /></el-icon>
        {{ item.sellerName }}
      </div>
      <div class="meta">
        <el-tag size="small">{{ item.category }}</el-tag>
        <span class="price">¥{{ item.price }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ item: Object })
const imageUrl = computed(() => props.item?.imageUrl || '')
</script>

<style scoped>
.item-card { cursor: pointer; margin-bottom: 16px; position: relative; }
.cover { width: 100%; height: 160px; border-radius: 4px; }
.img-placeholder {
  width: 100%; height: 160px;
  display: flex; align-items: center; justify-content: center;
  background: #f0f2f5; color: #909399;
}
.mine-tag { position: absolute; top: 12px; right: 12px; }
.title { font-weight: 600; margin: 8px 0 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.seller {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 6px;
}
.meta { display: flex; justify-content: space-between; align-items: center; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
</style>
