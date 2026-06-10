import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const store = useUserStore()
  if (store.token) {
    config.headers.Authorization = `Bearer ${store.token}`
  }
  return config
})

function resolveErrorMessage(err) {
  if (err.code === 'ERR_NETWORK' || !err.response) {
    return '无法连接服务器，请确认后端已启动（http://localhost:8080）'
  }
  const data = err.response.data
  if (data?.message) return data.message
  if (err.response.status === 404) return '接口不存在，请检查后端是否已更新并重启'
  if (err.response.status === 403) return '无权限或登录已过期'
  if (err.response.status === 500) return '服务器内部错误，请查看后端日志'
  return `请求失败（${err.response.status}）`
}

request.interceptors.response.use(
  res => {
    const data = res.data
    if (data.code !== 200) {
      if (!res.config?.silent) {
        ElMessage.error(data.message || '请求失败')
      }
      return Promise.reject(data)
    }
    return data
  },
  err => {
    const silent = err.config?.silent
    const store = useUserStore()
    const status = err.response?.status

    if ((status === 401 || status === 403) && store.token) {
      store.logout()
      if (!silent && status === 401) {
        router.push('/login')
      }
    }

    if (!silent) {
      ElMessage.error(resolveErrorMessage(err))
    }
    return Promise.reject(err)
  }
)

export default request
