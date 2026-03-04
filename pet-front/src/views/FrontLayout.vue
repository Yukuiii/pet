<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSiteConfig } from '@/api/siteConfig'
import { FRONT_NAV } from '@/constants/frontNav'
import { getMediaUrl } from '@/utils/url'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const route = useRoute()
const { user: me, clearUser } = useUserStore()

const siteConfig = ref(null)

/**
 * 加载站点基础配置
 * @returns {Promise<void>}
 */
const loadSiteConfig = async () => {
  try {
    const res = await getSiteConfig()
    if (res.code === 200) {
      siteConfig.value = res.data
    }
  } catch (e) {}
}

/**
 * 退出登录
 */
const logout = () => {
  clearUser()
  router.replace('/login')
}

/**
 * 判断导航菜单激活状态
 * @param {string} path - 菜单路径
 * @returns {boolean} 是否激活
 */
const isActive = (path) => {
  if (path === '/pets') {
    // 宠物详情页也归属宠物档案菜单
    return route.path === '/pets' || route.path.startsWith('/pets/')
  }
  if (path === '/knowledge') {
    // 文章详情页也归属养护知识菜单
    return route.path === '/knowledge' || route.path.startsWith('/knowledge/')
  }
  return route.path === path
}

const nav = FRONT_NAV

onMounted(loadSiteConfig)
</script>

<template>
  <div class="min-h-screen bg-transparent">
	    <div class="w-full px-3 py-4 sm:px-4 lg:px-5 lg:py-6">
	      <div class="grid grid-cols-1 gap-6 lg:grid-cols-[240px_1fr]">
	        <aside class="h-full rounded-2xl border border-slate-200/90 bg-white/90 p-5 shadow-sm backdrop-blur-[1px]">
          <div class="flex items-center gap-3">
            <img
              v-if="siteConfig?.logo"
              :src="getMediaUrl(siteConfig.logo)"
              alt="Logo"
              class="h-10 w-10 rounded-lg object-cover"
            />
            <div>
              <div class="text-base font-semibold text-slate-900">{{ siteConfig?.siteName || '宠物管理系统' }}</div>
              <div class="text-xs text-slate-500">宠物日常管理平台</div>
            </div>
          </div>

          <div class="mt-6">
            <div class="mb-2 text-xs font-medium tracking-wide text-slate-500">导航</div>
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
              <button
                v-if="me?.role === 'admin'"
                class="h-10 rounded-lg border px-3 text-left text-sm transition-colors"
                :class="isActive('/admin/users')
                  ? 'border-cyan-600 bg-cyan-600 text-white'
                  : 'border-slate-200 bg-white text-slate-700 hover:border-cyan-200 hover:bg-cyan-50'"
                @click="router.push('/admin/users')"
              >
                后台管理
              </button>
            </div>
          </div>

          <div class="mt-6 rounded-xl border border-cyan-100 bg-cyan-50/60 p-4">
            <div class="flex items-center gap-3">
              <img
                v-if="me?.avatar"
                :src="getMediaUrl(me.avatar)"
                alt="头像"
                class="h-11 w-11 rounded-full border border-slate-200 object-cover"
              />
              <div
                v-else
                class="flex h-11 w-11 items-center justify-center rounded-full bg-cyan-100 text-sm font-medium text-cyan-700"
              >
                {{ (me?.nickname || me?.username || 'U').charAt(0).toUpperCase() }}
              </div>
              <div class="min-w-0">
                <div class="truncate text-sm font-medium text-slate-900">{{ me?.nickname || me?.username || '用户' }}</div>
                <div class="truncate text-xs text-slate-500">@{{ me?.username || '-' }}</div>
              </div>
            </div>
            <button
              class="mt-3 h-9 w-full rounded-lg bg-cyan-600 text-sm text-white shadow-sm transition-colors hover:bg-cyan-500"
              @click="logout"
            >
              退出登录
            </button>
          </div>
        </aside>

	        <main class="min-w-0">
	          <router-view />
	        </main>
	      </div>

	      <footer
	        v-if="siteConfig"
	        class="mt-6 rounded-2xl border border-slate-200 bg-white/90 p-5 text-xs text-slate-500 shadow-sm backdrop-blur-[1px]"
	      >
	        <div class="flex flex-wrap items-center gap-x-6 gap-y-1">
	          <div v-if="siteConfig.contactEmail">联系邮箱：{{ siteConfig.contactEmail }}</div>
	          <div v-if="siteConfig.contactPhone">联系电话：{{ siteConfig.contactPhone }}</div>
	          <div v-if="siteConfig.icp">备案号：{{ siteConfig.icp }}</div>
	        </div>
	        <div v-if="siteConfig.footerText" class="mt-2 text-slate-400">{{ siteConfig.footerText }}</div>
	      </footer>
	    </div>
	  </div>
	</template>
