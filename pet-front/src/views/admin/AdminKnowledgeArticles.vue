<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  adminCreateKnowledgeArticle,
  adminDeleteKnowledgeArticle,
  adminGetKnowledgeArticle,
  adminListKnowledgeCategories,
  adminPageKnowledgeArticles,
  adminUpdateKnowledgeArticle
} from '@/api/adminKnowledge'

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const categories = ref([])
const keyword = ref('')
const categoryId = ref('')

const page = ref(1)
const size = ref(10)
const total = ref(0)
const list = ref([])
const totalPages = computed(() => Math.max(1, Math.ceil((total.value || 0) / size.value)))

const editingId = ref(null)
const form = ref({
  categoryId: '',
  title: '',
  summary: '',
  cover: '',
  content: ''
})

const loadCategories = async () => {
  try {
    const res = await adminListKnowledgeCategories()
    if (res.code === 200) categories.value = res.data || []
  } catch (e) {}
}

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (categoryId.value !== '') params.categoryId = Number(categoryId.value)
    const res = await adminPageKnowledgeArticles(params)
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

const resetForm = () => {
  editingId.value = null
  form.value.categoryId = ''
  form.value.title = ''
  form.value.summary = ''
  form.value.cover = ''
  form.value.content = ''
}

const edit = async (a) => {
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const res = await adminGetKnowledgeArticle(a.id)
    if (res.code === 200) {
      const d = res.data || {}
      editingId.value = d.id
      form.value.categoryId = d.categoryId != null ? String(d.categoryId) : ''
      form.value.title = d.title || ''
      form.value.summary = d.summary || ''
      form.value.cover = d.cover || ''
      form.value.content = d.content || ''
    } else {
      errorMsg.value = res.message || '加载失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const submit = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  const cid = form.value.categoryId === '' ? null : Number(form.value.categoryId)
  const title = form.value.title.trim()
  const content = form.value.content.trim()
  if (!cid) {
    errorMsg.value = '请选择分类'
    return
  }
  if (!title) {
    errorMsg.value = '标题不能为空'
    return
  }
  if (!content) {
    errorMsg.value = '正文不能为空'
    return
  }
  saving.value = true
  try {
    let res
    const payload = {
      categoryId: cid,
      title,
      summary: form.value.summary,
      cover: form.value.cover,
      content
    }
    if (editingId.value) {
      res = await adminUpdateKnowledgeArticle(editingId.value, payload)
    } else {
      res = await adminCreateKnowledgeArticle(payload)
    }
    if (res.code === 200) {
      successMsg.value = editingId.value ? '已保存' : '已发布'
      resetForm()
      await load()
    } else {
      errorMsg.value = res.message || '操作失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    saving.value = false
  }
}

const remove = async (a) => {
  errorMsg.value = ''
  successMsg.value = ''
  if (!confirm('确认删除该文章？')) return
  try {
    const res = await adminDeleteKnowledgeArticle(a.id)
    if (res.code === 200) {
      successMsg.value = '已删除'
      if (editingId.value === a.id) resetForm()
      await load()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const submitText = computed(() => (editingId.value ? '保存' : '发布'))

onMounted(loadCategories)
onMounted(load)
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-xl p-6">
    <div class="flex items-start justify-between gap-4 mb-5">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">知识文章管理</h2>
        <p class="text-sm text-gray-500 mt-1">发布、编辑、删除养护知识文章</p>
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
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-medium text-gray-900">{{ editingId ? '编辑文章' : '发布文章' }}</div>
        <button
          v-if="editingId"
          class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="resetForm"
        >
          取消编辑
        </button>
      </div>
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
        <select
          v-model="form.categoryId"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        >
          <option value="">选择分类</option>
          <option v-for="c in categories" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
        </select>
        <input
          v-model="form.title"
          type="text"
          placeholder="标题"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <input
          v-model="form.summary"
          type="text"
          placeholder="摘要（可选）"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <input
          v-model="form.cover"
          type="text"
          placeholder="封面URL（可选）"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <textarea
          v-model="form.content"
          rows="8"
          placeholder="正文"
          class="lg:col-span-2 w-full px-4 py-3 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        ></textarea>
        <div class="lg:col-span-2">
          <button
            class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 transition-all"
            :disabled="saving"
            @click="submit"
          >
            {{ saving ? '处理中...' : submitText }}
          </button>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-3 mb-5">
      <select
        v-model="categoryId"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @change="applyFilters"
      >
        <option value="">全部分类</option>
        <option v-for="c in categories" :key="c.id" :value="String(c.id)">{{ c.name }}</option>
      </select>
      <input
        v-model="keyword"
        type="text"
        placeholder="标题/正文关键字"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
      <div></div>
      <button
        class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-all"
        :disabled="loading"
        @click="applyFilters"
      >
        查询
      </button>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="text-left text-gray-500">
            <th class="py-2 pr-4">ID</th>
            <th class="py-2 pr-4">标题</th>
            <th class="py-2 pr-4">分类</th>
            <th class="py-2 pr-4">创建时间</th>
            <th class="py-2 pr-4">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="a in list"
            :key="a.id"
            class="border-t border-gray-100 text-gray-800"
          >
            <td class="py-3 pr-4">{{ a.id }}</td>
            <td class="py-3 pr-4">{{ a.title }}</td>
            <td class="py-3 pr-4">{{ a.categoryName }}</td>
            <td class="py-3 pr-4">{{ a.createTime }}</td>
            <td class="py-3 pr-4">
              <div class="flex items-center gap-2">
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="edit(a)"
                >
                  编辑
                </button>
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="remove(a)"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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

