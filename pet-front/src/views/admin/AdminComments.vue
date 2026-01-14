<script setup>
import { computed, onMounted, ref } from 'vue'
import { deleteComment, pageComments } from '@/api/adminModeration'

const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const keyword = ref('')
const postId = ref('')
const authorId = ref('')

const page = ref(1)
const size = ref(10)
const total = ref(0)
const list = ref([])

const totalPages = computed(() => Math.max(1, Math.ceil((total.value || 0) / size.value)))

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (postId.value.trim()) params.postId = Number(postId.value.trim())
    if (authorId.value.trim()) params.authorId = Number(authorId.value.trim())
    const res = await pageComments(params)
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

const handleDelete = async (c) => {
  errorMsg.value = ''
  successMsg.value = ''
  if (!confirm('确认删除该评论？')) return
  try {
    const res = await deleteComment(c.id)
    if (res.code === 200) {
      successMsg.value = '已删除'
      await load()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

onMounted(load)
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-xl p-6">
    <div class="flex items-start justify-between gap-4 mb-5">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">评论监管</h2>
        <p class="text-sm text-gray-500 mt-1">分页查看所有评论并删除违规内容</p>
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

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-3 mb-5">
      <input
        v-model="keyword"
        type="text"
        placeholder="评论关键字"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
      <input
        v-model="postId"
        type="text"
        placeholder="动态ID（可选）"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
      <input
        v-model="authorId"
        type="text"
        placeholder="评论用户ID（可选）"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
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
        v-for="c in list"
        :key="c.id"
        class="border border-gray-200 rounded-xl p-5"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="min-w-0">
            <div class="text-sm font-medium text-gray-900">
              {{ c.author?.nickname || '用户' }}（ID: {{ c.author?.id }}）
            </div>
            <div class="text-xs text-gray-500 mt-1">{{ c.createTime }}</div>
          </div>
          <button
            class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="handleDelete(c)"
          >
            删除
          </button>
        </div>
        <div class="mt-3 text-sm text-gray-800 whitespace-pre-wrap">
          {{ c.content }}
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

