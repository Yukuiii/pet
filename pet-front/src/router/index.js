import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import Pets from '../views/Pets.vue'
import PetDetail from '../views/PetDetail.vue'
import Community from '../views/Community.vue'

/**
 * 路由配置
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/pets',
    name: 'Pets',
    component: Pets
  },
  {
    path: '/pets/:id',
    name: 'PetDetail',
    component: PetDetail
  },
  {
    path: '/community',
    name: 'Community',
    component: Community
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
