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

/**
 * 退出登录
 */
const logout = () => {
  localStorage.removeItem('user')
  router.replace('/login')
}

/**
 * 判断菜单是否激活
 * @param {string} path - 菜单路径
 * @returns {boolean} 是否激活
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(`${path}/`)
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
  <div class="min-h-screen bg-transparent">
    <div class="w-full px-3 py-4 sm:px-4 lg:px-5 lg:py-6">
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-[240px_1fr]">
        <aside class="h-full rounded-2xl border border-slate-200/90 bg-white/90 p-5 shadow-sm backdrop-blur-[1px]">
          <div class="flex items-center gap-3">
            <div class="flex h-10 w-10 items-center justify-center rounded-lg bg-cyan-600 text-sm font-semibold text-white">
              管
            </div>
            <div>
              <div class="text-base font-semibold text-slate-900">后台管理</div>
              <div class="text-xs text-slate-500">{{ me?.nickname || me?.username || '管理员' }}</div>
            </div>
          </div>

          <div class="mt-6">
            <div class="mb-2 text-xs font-medium tracking-wide text-slate-500">菜单</div>
            <div class="grid grid-cols-2 gap-2 lg:grid-cols-1">
              <button
                v-for="item in nav"
                :key="item.path"
                class="h-10 rounded-lg border px-3 text-left text-sm transition-colors"
                :class="isActive(item.path)
                  ? 'border-cyan-600 bg-cyan-600 text-white'
                  : 'border-slate-200 bg-white text-slate-700 hover:border-cyan-200 hover:bg-cyan-50'"
                @click="router.push(item.path)"
              >
                {{ item.name }}
              </button>
            </div>
          </div>

          <div class="mt-6 rounded-xl border border-cyan-100 bg-cyan-50/60 p-4">
            <div class="truncate text-sm font-medium text-slate-900">
              {{ me?.nickname || me?.username || '管理员' }}
            </div>
            <div class="mt-1 truncate text-xs text-slate-500">@{{ me?.username || '-' }}</div>
            <button
              class="mt-3 h-9 w-full rounded-lg border border-slate-200 bg-white text-sm text-slate-700 transition-colors hover:border-cyan-200 hover:bg-cyan-50"
              @click="router.push('/')"
            >
              返回首页
            </button>
            <button
              class="mt-2 h-9 w-full rounded-lg bg-cyan-600 text-sm text-white shadow-sm transition-colors hover:bg-cyan-500"
              @click="logout"
            >
              退出登录
            </button>
          </div>
        </aside>

        <main class="min-w-0">
          <div class="mb-4 rounded-2xl border border-slate-200/90 bg-white/90 p-5 shadow-sm">
            <h1 class="text-xl font-semibold text-slate-900">后台管理</h1>
            <p class="mt-1 text-sm text-slate-500">集中管理用户、内容与站点配置</p>
          </div>
          <router-view />
        </main>
      </div>
    </div>
  </div>
</template>
