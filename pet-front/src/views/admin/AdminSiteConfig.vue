<script setup>
import { onMounted, ref } from 'vue'
import { getAdminSiteConfig, updateAdminSiteConfig } from '@/api/adminSiteConfig'

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = ref({
  siteName: '',
  logo: '',
  contactEmail: '',
  contactPhone: '',
  icp: '',
  footerText: ''
})

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const res = await getAdminSiteConfig()
    if (res.code === 200) {
      const d = res.data || {}
      form.value.siteName = d.siteName || ''
      form.value.logo = d.logo || ''
      form.value.contactEmail = d.contactEmail || ''
      form.value.contactPhone = d.contactPhone || ''
      form.value.icp = d.icp || ''
      form.value.footerText = d.footerText || ''
    } else {
      errorMsg.value = res.message || '加载失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  errorMsg.value = ''
  successMsg.value = ''
  const siteName = form.value.siteName.trim()
  if (!siteName) {
    errorMsg.value = '站点名称不能为空'
    return
  }
  saving.value = true
  try {
    const res = await updateAdminSiteConfig({
      siteName,
      logo: form.value.logo,
      contactEmail: form.value.contactEmail,
      contactPhone: form.value.contactPhone,
      icp: form.value.icp,
      footerText: form.value.footerText
    })
    if (res.code === 200) {
      successMsg.value = '已保存'
      await load()
    } else {
      errorMsg.value = res.message || '保存失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="bg-white border border-gray-200 rounded-xl p-6">
    <div class="flex items-start justify-between gap-4 mb-5">
      <div>
        <h2 class="text-lg font-semibold text-gray-900">网站配置</h2>
        <p class="text-sm text-gray-500 mt-1">维护站点名称、Logo、联系方式与页脚信息</p>
      </div>
      <div class="flex items-center gap-2">
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          :disabled="loading"
          @click="load"
        >
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
        <button
          class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors disabled:opacity-50"
          :disabled="saving"
          @click="save"
        >
          {{ saving ? '保存中...' : '保存' }}
        </button>
      </div>
    </div>

    <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
      {{ errorMsg }}
    </div>
    <div v-if="successMsg" class="mb-4 p-3 rounded-lg bg-green-50 text-green-700 text-sm">
      {{ successMsg }}
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">站点名称</label>
        <input
          v-model="form.siteName"
          type="text"
          placeholder="例如：宠物管理系统"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">Logo URL</label>
        <input
          v-model="form.logo"
          type="text"
          placeholder="http(s):// 或 /uploads/..."
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">联系邮箱</label>
        <input
          v-model="form.contactEmail"
          type="text"
          placeholder="support@example.com"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">联系电话</label>
        <input
          v-model="form.contactPhone"
          type="text"
          placeholder="可选"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">备案号</label>
        <input
          v-model="form.icp"
          type="text"
          placeholder="可选"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
      <div class="space-y-1.5">
        <label class="block text-sm font-medium text-gray-700">页脚文案</label>
        <input
          v-model="form.footerText"
          type="text"
          placeholder="可选"
          class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
        />
      </div>
    </div>
  </div>
</template>

