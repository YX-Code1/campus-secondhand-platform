import request from './request'

//接口
export const authApi = {
  //发送请求给/auth/login
  login: data => request.post('/auth/login', data),
  register: data => request.post('/auth/register', data)
}

export const userApi = {
  me: () => request.get('/users/me'),
  updateMe: data => request.put('/users/me', data),
  brief: id => request.get(`/users/${id}/brief`)
}

export const itemApi = {
  search: params => request.get('/items', { params }),
  hot: limit => request.get('/items/hot', { params: { limit } }),
  detail: id => request.get(`/items/${id}`),
  mine: params => request.get('/items/mine', { params }),
  create: data => request.post('/items', data),
  update: (id, data) => request.put(`/items/${id}`, data),
  remove: id => request.delete(`/items/${id}`),
  upload: file => {
    const form = new FormData()
    form.append('file', file)
    return request.post('/items/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const tradeApi = {
  create: itemId => request.post('/trades', { itemId }),
  mine: () => request.get('/trades/mine'),
  updateStatus: (id, status) => request.put(`/trades/${id}/status`, { status })
}

export const chatApi = {
  conversations: () => request.get('/chat/conversations'),
  messages: peerId => request.get('/chat/messages', { params: { peerId } }),
  send: data => request.post('/chat/messages', data),
  unreadCount: () => request.get('/chat/unread-count', { silent: true })
}

export const adminApi = {
  stats: () => request.get('/admin/stats'),
  users: params => request.get('/admin/users', { params }),
  updateUserStatus: (id, status) => request.put(`/admin/users/${id}/status`, { status }),
  deleteUser: id => request.delete(`/admin/users/${id}`),
  pendingAudit: () => request.get('/admin/items/audit'),
  auditItem: (id, auditStatus) => request.put(`/admin/items/${id}/audit`, { auditStatus }),
  offShelf: id => request.put(`/admin/items/${id}/off-shelf`)
}
