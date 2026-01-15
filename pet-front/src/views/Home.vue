<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { latestAnnouncement } from '@/api/announcement'
import { getSiteConfig } from '@/api/siteConfig'
import { listPosts } from '@/api/community'
import { getMediaUrl } from '@/utils/url'

const router = useRouter()

const userStr = ref(localStorage.getItem('user') || '')
const user = computed(() => {
  if (!userStr.value) return null
  try {
    return JSON.parse(userStr.value)
  } catch (e) {
    return null
  }
})

const toLogin = () => router.push('/login')
const toRegister = () => router.push('/register')
const toProfile = () => router.push('/profile')
const toPets = () => router.push('/pets')
const toCommunity = () => router.push('/community')
const toKnowledge = () => router.push('/knowledge')
const toAdmin = () => router.push('/admin/users')
const logout = () => {
  localStorage.removeItem('user')
  userStr.value = ''
  router.push('/login')
}

const latest = ref(null)
const loadLatest = async () => {
  try {
    const res = await latestAnnouncement()
    if (res.code === 200) {
      latest.value = res.data
    }
  } catch (e) {}
}

const siteConfig = ref(null)
const loadSiteConfig = async () => {
  try {
    const res = await getSiteConfig()
    if (res.code === 200) {
      siteConfig.value = res.data
    }
  } catch (e) {}
}

onMounted(loadLatest)
onMounted(loadSiteConfig)

const posts = ref([])
const postsLoading = ref(false)
const loadPosts = async () => {
  postsLoading.value = true
  try {
    const res = await listPosts()
    if (res.code === 200) {
      posts.value = (res.data || []).slice(0, 5)
    }
  } catch (e) {} finally {
    postsLoading.value = false
  }
}

onMounted(loadPosts)

const goCommunity = () => router.push('/community')
const goAnnouncements = () => router.push('/announcements')

/**
 * 格式化时间显示
 * @param {string} timeStr - 时间字符串
 * @returns {string} 格式化后的时间
 */
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return timeStr

  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')

  if (year === now.getFullYear()) {
    return `${month}-${day} ${hour}:${minute}`
  }
  return `${year}-${month}-${day} ${hour}:${minute}`
}
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-5xl mx-auto px-6 py-10">
      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-start justify-between gap-4">
          <div class="flex items-center gap-4">
            <img
              v-if="siteConfig?.logo"
              :src="getMediaUrl(siteConfig.logo)"
              alt="Logo"
              class="w-12 h-12 rounded-lg object-cover"
            />
            <div>
              <h1 class="text-2xl font-semibold text-gray-900">{{ siteConfig?.siteName || '宠物管理系统' }}</h1>
              <p class="text-sm text-gray-500 mt-1">欢迎使用</p>
            </div>
          </div>
          <div class="flex items-center gap-3">
            <template v-if="user">
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toProfile"
              >
                个人中心
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toPets"
              >
                宠物档案
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toCommunity"
              >
                社区动态
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toKnowledge"
              >
                养护知识
              </button>
              <button
                v-if="user.role === 'admin'"
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toAdmin"
              >
                后台管理
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
                @click="logout"
              >
                退出登录
              </button>
            </template>
            <template v-else>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toKnowledge"
              >
                养护知识
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="toLogin"
              >
                登录
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
                @click="toRegister"
              >
                注册
              </button>
            </template>
          </div>
        </div>

        <!-- 用户个人信息卡片 -->
        <div v-if="user" class="mt-6 rounded-xl border border-gray-200 bg-white p-5">
          <div class="flex items-center gap-4">
            <!-- 用户头像 -->
            <div class="flex-shrink-0">
              <img
                v-if="user.avatar"
                :src="getMediaUrl(user.avatar)"
                alt="头像"
                class="w-16 h-16 rounded-full object-cover border border-gray-200"
              />
              <div
                v-else
                class="w-16 h-16 rounded-full bg-gray-200 flex items-center justify-center text-gray-500 text-xl font-medium"
              >
                {{ (user.nickname || user.username || '').charAt(0).toUpperCase() }}
              </div>
            </div>
            <!-- 用户信息 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span class="text-lg font-semibold text-gray-900">{{ user.nickname || user.username }}</span>
                <span
                  v-if="user.role === 'admin'"
                  class="px-2 py-0.5 text-xs rounded-full bg-blue-100 text-blue-700"
                >
                  管理员
                </span>
              </div>
              <div class="mt-1 text-sm text-gray-500">@{{ user.username }}</div>
              <div v-if="user.email" class="mt-1 text-sm text-gray-500">{{ user.email }}</div>
            </div>
            <!-- 操作按钮 -->
            <div class="flex-shrink-0">
              <button
                class="h-9 px-4 rounded-lg bg-gray-100 text-sm text-gray-700 hover:bg-gray-200 transition-colors"
                @click="toProfile"
              >
                编辑资料
              </button>
            </div>
          </div>
        </div>

        <div class="mt-6 rounded-lg border border-gray-200 bg-gray-50 p-4 text-sm text-gray-700">
          <div v-if="siteConfig" class="mb-3 text-xs text-gray-500">
            <div v-if="siteConfig.contactEmail">联系邮箱：{{ siteConfig.contactEmail }}</div>
            <div v-if="siteConfig.contactPhone">联系电话：{{ siteConfig.contactPhone }}</div>
            <div v-if="siteConfig.icp">备案号：{{ siteConfig.icp }}</div>
            <div v-if="siteConfig.footerText">{{ siteConfig.footerText }}</div>
          </div>
          <div v-if="latest" class="mb-3">
            <div class="flex items-center justify-between gap-3">
              <div class="text-sm font-medium text-gray-900">最新公告：{{ latest.title }}</div>
              <button
                class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-xs text-gray-700 hover:bg-gray-100 transition-colors"
                @click="goAnnouncements"
              >
                更多公告
              </button>
            </div>
            <div class="mt-1 text-sm text-gray-700 whitespace-pre-wrap">{{ latest.content }}</div>
          </div>
          <div v-if="!user">
            先登录后即可使用个人信息管理功能。
          </div>
        </div>
      </div>

      <div class="mt-6 bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h2 class="text-lg font-semibold text-gray-900">社区动态</h2>
            <p class="text-sm text-gray-500 mt-1">最新动态预览</p>
          </div>
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="goCommunity"
          >
            查看更多
          </button>
        </div>

        <div v-if="postsLoading" class="text-sm text-gray-500">加载中...</div>
        <div v-else-if="posts.length === 0" class="text-sm text-gray-500">暂无动态。</div>
        <div v-else class="space-y-3">
          <div
            v-for="p in posts"
            :key="p.id"
            class="border border-gray-200 rounded-xl p-4"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="flex items-center gap-3 min-w-0">
                <div class="w-10 h-10 rounded-full bg-gray-100 border border-gray-200 overflow-hidden flex items-center justify-center flex-shrink-0">
                  <img v-if="p.author?.avatar" :src="getMediaUrl(p.author.avatar)" alt="avatar" class="w-full h-full object-cover" />
                  <div v-else class="text-xs text-gray-400">{{ (p.author?.nickname || '用户').charAt(0) }}</div>
                </div>
                <div class="min-w-0">
                  <div class="text-sm font-medium text-gray-900">
                    {{ p.author?.nickname || '用户' }}
                  </div>
                  <div class="text-xs text-gray-500">{{ formatTime(p.createTime) }}</div>
                </div>
              </div>
            </div>
            <div class="mt-3 text-sm text-gray-800 whitespace-pre-wrap">
              {{ p.content }}
            </div>
            <div v-if="p.images && p.images.length" class="mt-4 grid grid-cols-2 sm:grid-cols-3 gap-3">
              <img
                v-for="(img, idx) in p.images.slice(0, 3)"
                :key="idx"
                :src="getMediaUrl(img)"
                alt="img"
                class="w-full aspect-square object-cover rounded-lg border border-gray-200"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
