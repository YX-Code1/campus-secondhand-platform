<template>
  <div class="home">
    <el-card class="search-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="搜索物品" clearable />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="query.category" clearable placeholder="全部">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="query.minPrice" :min="0" placeholder="最低" />
          <span style="margin:0 8px">-</span>
          <el-input-number v-model="query.maxPrice" :min="0" placeholder="最高" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" v-if="hotItems.length" class="hot-section">
      <el-col :span="24"><h3>热门物品</h3></el-col>
      <el-col v-for="item in hotItems" :key="item.id" :xs="12" :sm="8" :md="6">
        <item-card :item="item" />
      </el-col>
    </el-row>

    <h3 class="section-title">
      全部物品
      <span v-if="userStore.isLogin" class="hint">（含您发布的待审核物品）</span>
    </h3>
    <el-row :gutter="16" v-loading="loading">
      <el-col v-for="item in items" :key="item.id" :xs="12" :sm="8" :md="6">
        <item-card :item="item" />
      </el-col>
    </el-row>
    <el-empty v-if="!loading && !items.length" description="暂无物品" />
    <div class="pager">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadItems"
      />
    </div>
  </div>
</template>

<script setup>
//ref响应式组件
import { ref, reactive, onMounted } from 'vue'
import { itemApi } from '../api'
import { useUserStore } from '../stores/user'
//组件导入，资料卡片
import ItemCard from '../components/ItemCard.vue'

const userStore = useUserStore()

const categories = ['书籍', '数码', '生活用品', '服饰', '运动', '其他']
const hotItems = ref([])
const items = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ keyword: '', category: '', minPrice: null, maxPrice: null, page: 1, size: 12 })

async function loadItems() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.keyword) delete params.keyword
    if (!params.category) delete params.category
    if (params.minPrice == null) delete params.minPrice
    if (params.maxPrice == null) delete params.maxPrice
    //调用接口，并获得返回的json
    const res = await itemApi.search(params)
    //获取并改变改变商品信息
    items.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1;
  loadItems()
}
function reset() {
  Object.assign(query, { keyword: '', category: '', minPrice: null, maxPrice: null, page: 1 })
  loadItems()
}
//页面挂载时执行
onMounted(async () => {
  try {
    const hot = await itemApi.hot(4)
    hotItems.value = hot.data || []
  } catch {
    hotItems.value = []
  }
  loadItems()
})
</script>

<style scoped>
.search-card { margin-bottom: 20px; }
.hot-section { margin-bottom: 24px; }
.section-title { margin-bottom: 16px; color: #303133; }
.hint { font-size: 13px; color: #909399; font-weight: normal; margin-left: 8px; }
.pager { margin-top: 24px; display: flex; justify-content: center; }
</style>
