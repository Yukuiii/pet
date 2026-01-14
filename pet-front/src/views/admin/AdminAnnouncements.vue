<script setup>
import { computed, onMounted, ref } from 'vue'
import { createAnnouncement, pageAnnouncements, updateAnnouncementStatus } from '@/api/adminAnnouncement'

const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const keyword = ref('')
const status = ref('')

const page = ref(1)
const size = ref(10)
const total = ref(0)
const list = ref([])
const totalPages = computed(() => Math.max(1, Math.ceil((total.value || 0) / size.value)))

const creating = ref(false)
const form = ref({
  title: '',
  content: '',
  status: 0
})

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (status.value !== '') params.status = Number(status.value)
    const res = await pageAnnouncements(params)
    if (res.code === 200) {
      total.value = res.data?.total || 0
      list.value = res.data?.list || []
    } else {
      errorMsg.value = res.message || '加载失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const applyFilters = async () => {
  page.value = 1
  await load()
}

const statusText = (s) => (s === 1 ? '无效' : '有效')

const toggleStatus = async (a) => {
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const next = a.status === 1 ? 0 : 1
    const res = await updateAnnouncementStatus(a.id, next)
    if (res.code === 200) {
      successMsg.value = '已更新状态'
      await load()
    } else {
      errorMsg.value = res.message || '更新失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const handleCreate = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  const title = form.value.title.trim()
  const content = form.value.content.trim()
  if (!title) {
    errorMsg.value = '标题不能为空'
    return
  }
  if (!content) {
    errorMsg.value = '内容不能为空'
    return
  }
  creating.value = true
  try {
    const res = await createAnnouncement({
      title,
      content,
      status: form.value.status
    })
    if (res.code === 200) {
      successMsg.value = '已发布'
      form.value.title = ''
      form.value.content = ''
      form.value.status = 0
      await load()
    } else {
      errorMsg.value = res.message || '发布失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    creating.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-xl p-6">
    <div class="flex items-start justify-between gap-4 mb-5">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">公告管理</h2>
        <p class="text-sm text-gray-500 mt-1">发布公告并设置有效/无效状态</p>
      </div>
      <button
        class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
        :disabled="loading"
        @click="load"
      >
        {{ loading ? '刷新中...' : '刷新' }}
      </button>
    </div>

    <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
      {{ errorMsg }}
    </div>
    <div v-if="successMsg" class="mb-4 p-3 rounded-lg bg-green-50 text-green-700 text-sm">
      {{ successMsg }}
    </div>

    <div class="border border-gray-200 rounded-xl p-5 mb-6">
      <div class="text-sm font-medium text-gray-900 mb-3">发布公告</div>
      <div class="space-y-3">
        <input
          v-model="form.title"
          type="text"
          placeholder="公告标题"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <textarea
          v-model="form.content"
          rows="4"
          placeholder="公告内容"
          class="w-full px-4 py-3 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        ></textarea>
        <div class="flex items-center justify-between gap-3">
          <select
            v-model="form.status"
            class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
          >
            <option :value="0">有效</option>
            <option :value="1">无效</option>
          </select>
          <button
            class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 transition-all"
            :disabled="creating"
            @click="handleCreate"
          >
            {{ creating ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-3 mb-5">
      <input
        v-model="keyword"
        type="text"
        placeholder="标题/内容关键字"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
      <select
        v-model="status"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @change="applyFilters"
      >
        <option value="">全部状态</option>
        <option value="0">有效</option>
        <option value="1">无效</option>
      </select>
      <button
        class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-all"
        :disabled="loading"
        @click="applyFilters"
      >
        查询
      </button>
    </div>

    <div v-if="!loading && list.length === 0" class="text-sm text-gray-500">暂无数据。</div>

    <div v-else class="space-y-3">
      <div
        v-for="a in list"
        :key="a.id"
        class="border border-gray-200 rounded-xl p-5"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="min-w-0">
            <div class="text-sm font-medium text-gray-900">
              {{ a.title }}
              <span class="ml-2 text-xs text-gray-500">（{{ statusText(a.status) }}）</span>
            </div>
            <div class="text-xs text-gray-500 mt-1">{{ a.createTime }}</div>
          </div>
          <button
            class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="toggleStatus(a)"
          >
            {{ a.status === 1 ? '设为有效' : '设为无效' }}
          </button>
        </div>
        <div class="mt-3 text-sm text-gray-800 whitespace-pre-wrap">
          {{ a.content }}
        </div>
      </div>
    </div>

    <div class="mt-5 flex items-center justify-between">
      <div class="text-sm text-gray-500">
        共 {{ total }} 条 · 第 {{ page }} / {{ totalPages }} 页
      </div>
      <div class="flex items-center gap-2">
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors disabled:opacity-50"
          :disabled="page <= 1"
          @click="page -= 1; load()"
        >
          上一页
        </button>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors disabled:opacity-50"
          :disabled="page >= totalPages"
          @click="page += 1; load()"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

