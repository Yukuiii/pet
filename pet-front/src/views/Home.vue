<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { latestAnnouncement } from '@/api/announcement'
import { getSiteConfig } from '@/api/siteConfig'
import { listPosts } from '@/api/community'

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

const postMediaUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return `http://localhost:8080${url}`
}

const goCommunity = () => router.push('/community')
const goAnnouncements = () => router.push('/announcements')
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-3xl mx-auto px-6 py-10">
      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-semibold text-gray-900">{{ siteConfig?.siteName || '宠物管理系统' }}</h1>
            <p class="text-sm text-gray-500 mt-1">欢迎使用</p>
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
          <div v-if="user">
            你好，{{ user.nickname || user.username }}。你可以在“个人中心”修改昵称、头像和密码。
          </div>
          <div v-else>
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
              <div class="min-w-0">
                <div class="text-sm font-medium text-gray-900">
                  {{ p.author?.nickname || '用户' }}（ID: {{ p.author?.id }}）
                </div>
                <div class="text-xs text-gray-500 mt-1">{{ p.createTime }}</div>
              </div>
            </div>
            <div class="mt-3 text-sm text-gray-800 whitespace-pre-wrap">
              {{ p.content }}
            </div>
            <div v-if="p.images && p.images.length" class="mt-4 grid grid-cols-2 sm:grid-cols-3 gap-3">
              <img
                v-for="(img, idx) in p.images.slice(0, 3)"
                :key="idx"
                :src="postMediaUrl(img)"
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
