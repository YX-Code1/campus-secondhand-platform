<template>
  <div class="messages-page">
    <el-card class="chat-card">
      <div class="chat-layout">
        <aside class="conv-list">
          <div class="conv-title">消息列表</div>
          <el-empty v-if="!conversations.length" description="暂无会话，从物品详情联系卖家" />
          <div
            v-for="c in conversations"
            :key="c.peerId"
            class="conv-item"
            :class="{ active: peerId === c.peerId }"
            @click="selectPeer(c)"
          >
            <el-avatar :size="40">{{ (c.peerName || '?').charAt(0) }}</el-avatar>
            <div class="conv-body">
              <div class="conv-top">
                <span class="conv-name">{{ c.peerName }}</span>
                <span class="conv-time">{{ formatTime(c.lastTime) }}</span>
              </div>
              <div class="conv-preview">
                <span class="preview-text">{{ c.lastMessage }}</span>
                <el-badge v-if="c.unreadCount" :value="c.unreadCount" />
              </div>
            </div>
          </div>
        </aside>
        <main class="chat-main">
          <template v-if="peerId">
            <div class="chat-header">
              <el-avatar :size="32">{{ (peerName || '?').charAt(0) }}</el-avatar>
              <span>{{ peerName }}</span>
              <span class="peer-user">@{{ peerUsername }}</span>
            </div>
            <!-- 每条消息的逻辑 -->
            <div ref="msgBoxRef" class="msg-list" v-loading="loading">
              <div
                  v-for="m in messages"
                  :key="m.id"
                  class="msg-row"
                  :class="{ mine: m.mine }"
              >
                <div class="bubble">{{ m.content }}</div>
                <div class="msg-time">{{ formatTime(m.createTime) }}</div>
              </div>
              <el-empty v-if="!loading && !messages.length" description="暂无消息，发一句打个招呼吧" />
            </div>
            <div class="msg-input">
              <!-- 聊天框 -->
              <el-input
                v-model="draft"
                type="textarea"
                :rows="2"
                placeholder="输入消息，Enter 发送"
                maxlength="500"
                show-word-limit
                @keydown.enter.exact.prevent="send"
              />
              <el-button type="primary" :loading="sending" @click="send">发送</el-button>
            </div>
          </template>
          <!-- 如果是空的 -->
          <el-empty v-else description="选择左侧会话，或从物品详情点击「联系卖家」" />
        </main>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { chatApi } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const conversations = ref([])
//存放ChatMessageVO的内容
const messages = ref([])
const peerId = ref(null)
const peerName = ref('')
const peerUsername = ref('')
const draft = ref('')
const loading = ref(false)
const sending = ref(false)
const msgBoxRef = ref(null)

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').slice(0, 16)
}

async function loadConversations() {
  const res = await chatApi.conversations()
  conversations.value = res.data || []
}

async function loadMessages() {
  if (!peerId.value) return
  loading.value = true
  try {
    const res = await chatApi.messages(peerId.value)
    messages.value = res.data || []
    await nextTick()
    const el = msgBoxRef.value
    if (el) el.scrollTop = el.scrollHeight
  } finally {
    loading.value = false
  }
}

function selectPeer(c) {
  peerId.value = c.peerId
  peerName.value = c.peerName
  peerUsername.value = c.peerUsername
  router.replace({ path: '/messages', query: { peerId: c.peerId, name: c.peerName } })
  loadMessages()
}

async function send() {
  const text = draft.value.trim()
  if (!text || !peerId.value) return
  sending.value = true
  try {
    await chatApi.send({ peerId: peerId.value, content: text })
    draft.value = ''
    //发送完后刷新下渲染
    await loadMessages()
    await loadConversations()
  } catch (e) {
    ElMessage.error(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

function initFromQuery() {
  const q = route.query
  if (q.peerId) {
    peerId.value = Number(q.peerId)
    peerName.value = q.name || ''
    const found = conversations.value.find(c => c.peerId === peerId.value)
    if (found) peerUsername.value = found.peerUsername
    loadMessages()
  }
}
//挂载
onMounted(async () => {
  await loadConversations()
  initFromQuery()
})

watch(() => route.query.peerId, async () => {
  await loadConversations()
  initFromQuery()
})
</script>

<style scoped>
.messages-page { max-width: 1100px; margin: 0 auto; }
.chat-card { min-height: 560px; }
.chat-layout { display: flex; height: 520px; }
.conv-list {
  width: 280px;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
  flex-shrink: 0;
}
.conv-title {
  padding: 12px 16px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
}
.conv-item {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background .2s;
}
.conv-item:hover, .conv-item.active { background: #ecf5ff; }
.conv-body { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; align-items: center; }
.conv-name { font-weight: 500; font-size: 14px; }
.conv-time { font-size: 11px; color: #909399; }
.conv-preview { display: flex; justify-content: space-between; margin-top: 4px; }
.preview-text {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-header {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
.peer-user { font-size: 12px; color: #909399; font-weight: normal; }
.msg-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}
.msg-row { margin-bottom: 12px; max-width: 75%; }
.msg-row.mine { margin-left: auto; text-align: right; }
.bubble {
  display: inline-block;
  padding: 10px 14px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0,0,0,.06);
  word-break: break-word;
  text-align: left;
}
.msg-row.mine .bubble { background: #409eff; color: #fff; }
.msg-time { font-size: 11px; color: #909399; margin-top: 4px; }
.msg-input {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
  align-items: flex-end;
}
.msg-input .el-textarea { flex: 1; }
</style>
