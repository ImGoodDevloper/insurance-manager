import { createRouter, createWebHistory } from 'vue-router'
import UserLogin from '@/views/UserLogin.vue' // 確保路徑正確
import PolicyManager from '@/views/PolicyManager.vue'

const routes = [
  {
    path: '/',
    redirect: '/login' // 預設首頁直接轉向登入
  },
  {
    path: '/login',
    name: 'UserLogin',
    component: UserLogin
  },
  {
    path: '/policies',
    name: 'Policies',
    component: PolicyManager
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router