import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

//前端路由配置，指定访问URL的vue组件渲染
const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'items/:id', name: 'ItemDetail', component: () => import('../views/ItemDetail.vue') },
      { path: 'publish', name: 'Publish', component: () => import('../views/Publish.vue'), meta: { auth: true } },
      { path: 'my-items', name: 'MyItems', component: () => import('../views/MyItems.vue'), meta: { auth: true } },
      { path: 'trades', name: 'Trades', component: () => import('../views/Trades.vue'), meta: { auth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
      { path: 'messages', name: 'Messages', component: () => import('../views/Messages.vue'), meta: { auth: true } },
      { path: 'admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { auth: true, admin: true } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

//全局前置守卫，在页面跳转之前做权限判断，进行拦截
router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.meta.auth && !store.isLogin) return next('/login')
  if (to.meta.admin && !store.isAdmin) return next('/')
  if (to.meta.guest && store.isLogin) return next('/')
  next()
})

export default router
