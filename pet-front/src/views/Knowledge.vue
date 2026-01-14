<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listArticles, listCategories } from '@/api/knowledge'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')

const categories = ref([])
const activeCategory = ref(null)
const keyword = ref('')

const articles = ref([])

const loadCategories = async () => {
  try {
    const res = await listCategories()
    if (res.code === 200) {
      categories.value = res.data || []
    } else {
      errorMsg.value = res.message || '获取分类失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const loadArticles = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const params = {}
    if (activeCategory.value) params.categoryId = activeCategory.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await listArticles(params)
    if (res.code === 200) {
      articles.value = res.data || []
    } else {
      errorMsg.value = res.message || '获取文章列表失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const toDetail = (id) => router.push(`/knowledge/${id}`)

onMounted(async () => {
  await loadCategories()
  await loadArticles()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-5xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">养护知识</h1>
          <p class="text-sm text-gray-500 mt-1">按分类检索宠物养护文章</p>
        </div>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="router.push('/')"
        >
          返回首页
        </button>
      </div>

      <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
        {{ errorMsg }}
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6 mb-6">
        <div class="flex flex-wrap items-center gap-3">
          <button
            class="h-10 px-4 rounded-lg text-sm transition-all"
            :class="activeCategory === null
              ? 'bg-gray-900 text-white'
              : 'bg-white border border-gray-200 text-gray-700 hover:bg-gray-100'"
            @click="activeCategory = null; loadArticles()"
          >
            全部
          </button>
          <button
            v-for="c in categories"
            :key="c.id"
            class="h-10 px-4 rounded-lg text-sm transition-all"
            :class="activeCategory === c.id
              ? 'bg-gray-900 text-white'
              : 'bg-white border border-gray-200 text-gray-700 hover:bg-gray-100'"
            @click="activeCategory = c.id; loadArticles()"
          >
            {{ c.name }}
          </button>
          <div class="ml-auto flex items-center gap-2">
            <input
              v-model="keyword"
              type="text"
              placeholder="关键字"
              class="h-10 px-3 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
            <button
              class="h-10 px-4 rounded-lg bg-gray-900 text-white text-sm hover:bg-gray-800 transition-colors"
              @click="loadArticles"
            >
              搜索
            </button>
          </div>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">文章列表</h2>
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            :disabled="loading"
            @click="loadArticles"
          >
            {{ loading ? '刷新中...' : '刷新' }}
          </button>
        </div>
        <div v-if="!loading && articles.length === 0" class="text-sm text-gray-500">暂无文章。</div>
        <div v-else class="space-y-3">
          <div
            v-for="a in articles"
            :key="a.id"
            class="flex items-center gap-4 p-4 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors"
          >
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-gray-900 truncate">
                {{ a.title }}
              </div>
              <div class="text-xs text-gray-500 mt-1">
                {{ a.categoryName || '未分类' }}
                <span class="mx-2">·</span>
                {{ a.summary || '——' }}
              </div>
            </div>
            <button
              class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
              @click="toDetail(a.id)"
            >
              查看
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

