<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const me = computed(() => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return null
  try {
    return JSON.parse(userStr)
  } catch (e) {
    return null
  }
})

const logout = () => {
  localStorage.removeItem('user')
  router.replace('/login')
}

const nav = [
  { name: '用户管理', path: '/admin/users' },
  { name: '动态监管', path: '/admin/posts' },
  { name: '评论监管', path: '/admin/comments' },
  { name: '公告管理', path: '/admin/announcements' },
  { name: '网站配置', path: '/admin/site-config' },
  { name: '知识分类', path: '/admin/knowledge-categories' },
  { name: '知识文章', path: '/admin/knowledge-articles' }
]
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-6xl mx-auto px-6 py-8">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">后台管理</h1>
          <p class="text-sm text-gray-500 mt-1">
            {{ me?.nickname || me?.username || '管理员' }}
          </p>
        </div>
        <div class="flex items-center gap-3">
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="router.push('/')"
          >
            返回首页
          </button>
          <button
            class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
            @click="logout"
          >
            退出登录
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-[220px_1fr] gap-6">
        <aside class="bg-white border border-gray-200 rounded-xl p-4">
          <div class="text-xs font-medium text-gray-500 mb-3">菜单</div>
          <div class="space-y-2">
            <button
              v-for="item in nav"
              :key="item.path"
              class="w-full h-10 px-3 rounded-lg text-sm text-left transition-colors"
              :class="route.path === item.path
                ? 'bg-gray-900 text-white'
                : 'bg-white border border-gray-200 text-gray-700 hover:bg-gray-100'"
              @click="router.push(item.path)"
            >
              {{ item.name }}
            </button>
          </div>
        </aside>

        <main class="min-w-0">
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>
