<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { latestAnnouncement } from '@/api/announcement'
import { getSiteConfig } from '@/api/siteConfig'
import { listPosts } from '@/api/community'
import { getMediaUrl } from '@/utils/url'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const { user } = useUserStore()

const toProfile = () => router.push('/profile')

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
  <div class="space-y-6">
    <section class="rounded-2xl border border-slate-200 bg-white p-6">
      <div class="flex items-start gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-slate-900">欢迎回来</h1>
          <p class="mt-1 text-sm text-slate-500">
            {{ user ? `今天也和 ${user.nickname || user.username} 一起照顾好毛孩子。` : '登录后开启完整的宠物管理体验。' }}
          </p>
        </div>
      </div>

      <div class="mt-6 grid grid-cols-1 gap-4 xl:grid-cols-[1.2fr_1fr]">
        <div
          v-if="user"
          class="rounded-xl border border-slate-200 bg-white p-5"
        >
          <div class="flex items-center gap-4">
            <div class="flex-shrink-0">
              <img
                v-if="user.avatar"
                :src="getMediaUrl(user.avatar)"
                alt="头像"
                class="h-16 w-16 rounded-full border border-slate-200 object-cover"
              />
              <div
                v-else
                class="flex h-16 w-16 items-center justify-center rounded-full bg-slate-200 text-xl font-medium text-slate-600"
              >
                {{ (user.nickname || user.username || '').charAt(0).toUpperCase() }}
              </div>
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex items-center gap-2">
                <span class="text-lg font-semibold text-slate-900">{{ user.nickname || user.username }}</span>
                <span
                  v-if="user.role === 'admin'"
                  class="rounded-full bg-blue-100 px-2 py-0.5 text-xs text-blue-700"
                >
                  管理员
                </span>
              </div>
              <div class="mt-1 text-sm text-slate-500">@{{ user.username }}</div>
              <div v-if="user.email" class="mt-1 text-sm text-slate-500">{{ user.email }}</div>
            </div>
            <button
              class="h-9 rounded-lg bg-slate-100 px-4 text-sm text-slate-700 transition-colors hover:bg-slate-200"
              @click="toProfile"
            >
              编辑资料
            </button>
          </div>
        </div>
        <div
          v-else
          class="rounded-xl border border-slate-200 bg-slate-50 p-5 text-sm text-slate-600"
        >
          先登录后即可使用个人信息管理功能，并同步你的宠物档案数据。
        </div>

        <div class="rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-700">
          <div v-if="siteConfig" class="space-y-1 text-xs text-slate-500">
            <div v-if="siteConfig.contactEmail">联系邮箱：{{ siteConfig.contactEmail }}</div>
            <div v-if="siteConfig.contactPhone">联系电话：{{ siteConfig.contactPhone }}</div>
            <div v-if="siteConfig.icp">备案号：{{ siteConfig.icp }}</div>
            <div v-if="siteConfig.footerText">{{ siteConfig.footerText }}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="rounded-2xl border border-slate-200 bg-white p-6">
      <div class="mb-4 flex items-center justify-between">
        <div>
          <h2 class="text-lg font-semibold text-slate-900">社区动态</h2>
          <p class="mt-1 text-sm text-slate-500">最新动态预览</p>
        </div>
        <button
          class="h-10 rounded-lg border border-slate-200 bg-white px-4 text-sm text-slate-700 transition-colors hover:bg-cyan-50"
          @click="goCommunity"
        >
          查看更多
        </button>
      </div>

      <div
        v-if="latest"
        class="mb-4 rounded-xl border border-slate-200 bg-slate-50 p-4"
      >
        <div class="flex items-center justify-between gap-3">
          <div class="truncate text-sm font-medium text-slate-900">最新公告：{{ latest.title }}</div>
          <button
            class="h-8 rounded-lg border border-slate-200 bg-white px-3 text-xs text-slate-700 transition-colors hover:bg-cyan-50"
            @click="goAnnouncements"
          >
            更多公告
          </button>
        </div>
        <div class="mt-2 whitespace-pre-wrap text-sm text-slate-700">{{ latest.content }}</div>
      </div>

      <div v-if="postsLoading" class="text-sm text-slate-500">加载中...</div>
      <div v-else-if="posts.length === 0" class="text-sm text-slate-500">暂无动态。</div>
      <div v-else class="grid grid-cols-1 gap-3 xl:grid-cols-2">
        <article
          v-for="p in posts"
          :key="p.id"
          class="rounded-xl border border-slate-200 p-4"
        >
          <div class="flex items-start justify-between gap-4">
            <div class="min-w-0 flex items-center gap-3">
              <div class="flex h-10 w-10 flex-shrink-0 items-center justify-center overflow-hidden rounded-full border border-slate-200 bg-slate-100">
                <img v-if="p.author?.avatar" :src="getMediaUrl(p.author.avatar)" alt="avatar" class="h-full w-full object-cover" />
                <div v-else class="text-xs text-slate-400">{{ (p.author?.nickname || '用户').charAt(0) }}</div>
              </div>
              <div class="min-w-0">
                <div class="text-sm font-medium text-slate-900">
                  {{ p.author?.nickname || '用户' }}
                </div>
                <div class="text-xs text-slate-500">{{ formatTime(p.createTime) }}</div>
              </div>
            </div>
          </div>
          <div class="mt-3 whitespace-pre-wrap text-sm text-slate-800">
            {{ p.content }}
          </div>
          <div v-if="p.images && p.images.length" class="mt-4 grid grid-cols-3 gap-2">
            <img
              v-for="(img, idx) in p.images.slice(0, 3)"
              :key="idx"
              :src="getMediaUrl(img)"
              alt="img"
              class="aspect-square w-full rounded-lg border border-slate-200 object-cover"
            />
          </div>
        </article>
      </div>
    </section>
  </div>
</template>
