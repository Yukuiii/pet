<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  adminCreateKnowledgeCategory,
  adminDeleteKnowledgeCategory,
  adminListKnowledgeCategories,
  adminUpdateKnowledgeCategory
} from '@/api/adminKnowledge'

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const keyword = ref('')
const list = ref([])

const editingId = ref(null)
const form = ref({
  name: '',
  sort: 0
})

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = {}
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await adminListKnowledgeCategories(params)
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

const resetForm = () => {
  editingId.value = null
  form.value.name = ''
  form.value.sort = 0
}

const submit = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  const name = form.value.name.trim()
  if (!name) {
    errorMsg.value = '分类名称不能为空'
    return
  }
  saving.value = true
  try {
    let res
    if (editingId.value) {
      res = await adminUpdateKnowledgeCategory(editingId.value, {
        name,
        sort: Number(form.value.sort || 0)
      })
    } else {
      res = await adminCreateKnowledgeCategory({
        name,
        sort: Number(form.value.sort || 0)
      })
    }
    if (res.code === 200) {
      successMsg.value = editingId.value ? '已保存' : '已创建'
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

const edit = (c) => {
  editingId.value = c.id
  form.value.name = c.name || ''
  form.value.sort = c.sort ?? 0
}

const remove = async (c) => {
  errorMsg.value = ''
  successMsg.value = ''
  if (!confirm('确认删除该分类？')) return
  try {
    const res = await adminDeleteKnowledgeCategory(c.id)
    if (res.code === 200) {
      successMsg.value = '已删除'
      if (editingId.value === c.id) resetForm()
      await load()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const submitText = computed(() => (editingId.value ? '保存' : '创建'))

onMounted(load)
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-xl p-6">
    <div class="flex items-start justify-between gap-4 mb-5">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">知识分类管理</h2>
        <p class="text-sm text-gray-500 mt-1">创建、编辑、删除养护知识分类</p>
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
      <div class="text-sm font-medium text-gray-900 mb-3">{{ editingId ? '编辑分类' : '创建分类' }}</div>
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-3">
        <input
          v-model="form.name"
          type="text"
          placeholder="分类名称"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <input
          v-model="form.sort"
          type="number"
          placeholder="排序（越大越靠前）"
          class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
        <div class="flex items-center gap-2">
          <button
            class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 transition-all"
            :disabled="saving"
            @click="submit"
          >
            {{ saving ? '处理中...' : submitText }}
          </button>
          <button
            v-if="editingId"
            class="h-11 px-5 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="resetForm"
          >
            取消
          </button>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-3 mb-5">
      <input
        v-model="keyword"
        type="text"
        placeholder="分类关键字"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="load"
      />
      <div></div>
      <button
        class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-all"
        :disabled="loading"
        @click="load"
      >
        查询
      </button>
    </div>

    <div class="overflow-x-auto">
      <table class="min-w-full text-sm">
        <thead>
          <tr class="text-left text-gray-500">
            <th class="py-2 pr-4">ID</th>
            <th class="py-2 pr-4">名称</th>
            <th class="py-2 pr-4">排序</th>
            <th class="py-2 pr-4">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="c in list"
            :key="c.id"
            class="border-t border-gray-100 text-gray-800"
          >
            <td class="py-3 pr-4">{{ c.id }}</td>
            <td class="py-3 pr-4">{{ c.name }}</td>
            <td class="py-3 pr-4">{{ c.sort }}</td>
            <td class="py-3 pr-4">
              <div class="flex items-center gap-2">
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="edit(c)"
                >
                  编辑
                </button>
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="remove(c)"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

