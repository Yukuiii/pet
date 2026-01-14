<script setup>
import { computed, onMounted, ref } from 'vue'
import { pageUsers, updateUserRole, updateUserStatus } from '@/api/adminUser'

const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const keyword = ref('')
const status = ref('')
const role = ref('')

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
    if (status.value !== '') params.status = Number(status.value)
    if (role.value !== '') params.role = role.value
    const res = await pageUsers(params)
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

const roleText = (r) => (r === 'admin' ? '管理员' : '普通用户')
const statusText = (s) => (s === 1 ? '封禁' : '正常')

const toggleStatus = async (u) => {
  successMsg.value = ''
  errorMsg.value = ''
  try {
    const next = u.status === 1 ? 0 : 1
    const res = await updateUserStatus(u.id, next)
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

const toggleRole = async (u) => {
  successMsg.value = ''
  errorMsg.value = ''
  try {
    const next = u.role === 'admin' ? 'user' : 'admin'
    const res = await updateUserRole(u.id, next)
    if (res.code === 200) {
      successMsg.value = '已更新角色'
      await load()
    } else {
      errorMsg.value = res.message || '更新失败'
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
        <h2 class="text-lg font-semibold text-gray-900">用户管理</h2>
        <p class="text-sm text-gray-500 mt-1">查询用户、封禁/解封、授予/撤销管理员</p>
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
        placeholder="用户名/邮箱/昵称"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @keyup.enter="applyFilters"
      />
      <select
        v-model="status"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @change="applyFilters"
      >
        <option value="">全部状态</option>
        <option value="0">正常</option>
        <option value="1">封禁</option>
      </select>
      <select
        v-model="role"
        class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        @change="applyFilters"
      >
        <option value="">全部角色</option>
        <option value="user">普通用户</option>
        <option value="admin">管理员</option>
      </select>
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
            <th class="py-2 pr-4">用户名</th>
            <th class="py-2 pr-4">邮箱</th>
            <th class="py-2 pr-4">昵称</th>
            <th class="py-2 pr-4">角色</th>
            <th class="py-2 pr-4">状态</th>
            <th class="py-2 pr-4">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="u in list"
            :key="u.id"
            class="border-t border-gray-100 text-gray-800"
          >
            <td class="py-3 pr-4">{{ u.id }}</td>
            <td class="py-3 pr-4">{{ u.username }}</td>
            <td class="py-3 pr-4">{{ u.email }}</td>
            <td class="py-3 pr-4">{{ u.nickname }}</td>
            <td class="py-3 pr-4">{{ roleText(u.role) }}</td>
            <td class="py-3 pr-4">{{ statusText(u.status) }}</td>
            <td class="py-3 pr-4">
              <div class="flex items-center gap-2">
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="toggleStatus(u)"
                >
                  {{ u.status === 1 ? '解封' : '封禁' }}
                </button>
                <button
                  class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="toggleRole(u)"
                >
                  {{ u.role === 'admin' ? '撤销管理员' : '设为管理员' }}
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
