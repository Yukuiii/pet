<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticle } from '@/api/knowledge'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

const loading = ref(false)
const errorMsg = ref('')
const article = ref(null)

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getArticle(id)
    if (res.code === 200) {
      article.value = res.data
    } else {
      errorMsg.value = res.message || '获取文章失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-5xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">文章详情</h1>
          <p class="text-sm text-gray-500 mt-1">{{ article?.categoryName || '' }}</p>
        </div>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="router.push('/knowledge')"
        >
          返回列表
        </button>
      </div>

      <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
        {{ errorMsg }}
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div v-if="!article && !loading" class="text-sm text-gray-500">内容不存在。</div>
        <template v-else-if="article">
          <h2 class="text-xl font-semibold text-gray-900">{{ article.title }}</h2>
          <p class="mt-2 text-sm text-gray-600">{{ article.summary }}</p>
          <div class="mt-6 prose prose-sm max-w-none">
            <pre class="whitespace-pre-wrap text-sm text-gray-800">{{ article.content }}</pre>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

