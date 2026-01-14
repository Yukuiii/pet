<script setup>
import { computed, onMounted, ref } from 'vue'
import { listAnnouncements } from '@/api/announcement'

const loading = ref(false)
const errorMsg = ref('')
const list = ref([])

const limit = ref(20)

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await listAnnouncements({ limit: limit.value })
    if (res.code === 200) {
      list.value = res.data || []
    } else {
      errorMsg.value = res.message || '加载失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const hasMoreHint = computed(() => (list.value?.length || 0) >= limit.value)

onMounted(load)
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-3xl mx-auto px-6 py-10">
      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-start justify-between gap-4 mb-5">
          <div>
            <h1 class="text-2xl font-semibold text-gray-900">公告</h1>
            <p class="text-sm text-gray-500 mt-1">仅展示有效公告（状态为 0）</p>
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

        <div v-if="loading" class="text-sm text-gray-500">加载中...</div>
        <div v-else-if="list.length === 0" class="text-sm text-gray-500">暂无公告。</div>
        <div v-else class="space-y-3">
          <div
            v-for="a in list"
            :key="a.id"
            class="border border-gray-200 rounded-xl p-5"
          >
            <div class="text-sm font-medium text-gray-900">{{ a.title }}</div>
            <div class="text-xs text-gray-500 mt-1">{{ a.createTime }}</div>
            <div class="mt-3 text-sm text-gray-800 whitespace-pre-wrap">{{ a.content }}</div>
          </div>
        </div>

        <div v-if="hasMoreHint" class="mt-4 text-xs text-gray-500">
          仅展示最近 {{ limit }} 条公告。
        </div>
      </div>
    </div>
  </div>
</template>

