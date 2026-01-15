import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import Pets from '../views/Pets.vue'
import PetDetail from '../views/PetDetail.vue'
import Community from '../views/Community.vue'
import Knowledge from '../views/Knowledge.vue'
import KnowledgeDetail from '../views/KnowledgeDetail.vue'
import Announcements from '../views/Announcements.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminPosts from '../views/admin/AdminPosts.vue'
import AdminComments from '../views/admin/AdminComments.vue'
import AdminAnnouncements from '../views/admin/AdminAnnouncements.vue'
import AdminSiteConfig from '../views/admin/AdminSiteConfig.vue'
import AdminKnowledgeCategories from '../views/admin/AdminKnowledgeCategories.vue'
import AdminKnowledgeArticles from '../views/admin/AdminKnowledgeArticles.vue'

/**
 * 路由配置
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
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
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/pets',
    name: 'Pets',
    component: Pets,
    meta: { requiresAuth: true }
  },
  {
    path: '/pets/:id',
    name: 'PetDetail',
    component: PetDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/community',
    name: 'Community',
    component: Community,
    meta: { requiresAuth: true }
  },
  {
    path: '/knowledge',
    name: 'Knowledge',
    component: Knowledge,
    meta: { requiresAuth: true }
  },
  {
    path: '/knowledge/:id',
    name: 'KnowledgeDetail',
    component: KnowledgeDetail,
    meta: { requiresAuth: true }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: Announcements,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: AdminPosts
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: AdminComments
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: AdminAnnouncements
      },
      {
        path: 'site-config',
        name: 'AdminSiteConfig',
        component: AdminSiteConfig
      },
      {
        path: 'knowledge-categories',
        name: 'AdminKnowledgeCategories',
        component: AdminKnowledgeCategories
      },
      {
        path: 'knowledge-articles',
        name: 'AdminKnowledgeArticles',
        component: AdminKnowledgeArticles
      }
    ]
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const userStr = localStorage.getItem('user')
  let user = null

  if (userStr) {
    try {
      user = JSON.parse(userStr)
    } catch (e) {
      localStorage.removeItem('user')
    }
  }

  // 需要登录的页面
  if (to.meta.requiresAuth && !user) {
    return '/login'
  }

  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && user?.role !== 'admin') {
    return '/'
  }

  return true
})

export default router
