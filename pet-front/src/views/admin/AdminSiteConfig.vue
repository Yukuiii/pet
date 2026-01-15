<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAdminSiteConfig, updateAdminSiteConfig, uploadSiteConfigLogo } from '@/api/adminSiteConfig'
import { getMediaUrl } from '@/utils/url'

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

// 待上传的 Logo 文件
const pendingLogoFile = ref(null)

/**
 * Logo 预览 URL
 * 如果有待上传的文件则显示本地预览，否则显示已保存的 Logo
 */
const logoPreviewUrl = computed(() => {
  if (pendingLogoFile.value) {
    return URL.createObjectURL(pendingLogoFile.value)
  }
  if (!form.value.logo) return ''
  return getMediaUrl(form.value.logo)
})

const load = async () => {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  pendingLogoFile.value = null
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

/**
 * 处理 Logo 文件选择（仅预览，不立即上传）
 * @param {Event} event 文件选择事件
 */
const handleLogoFile = (event) => {
  const file = event.target.files?.[0]
  if (!file) return
  event.target.value = ''
  pendingLogoFile.value = file
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
    // 如果有待上传的 Logo 文件，先上传
    if (pendingLogoFile.value) {
      const uploadRes = await uploadSiteConfigLogo(pendingLogoFile.value)
      if (uploadRes.code === 200) {
        pendingLogoFile.value = null
      } else {
        errorMsg.value = uploadRes.message || 'Logo 上传失败'
        saving.value = false
        return
      }
    }

    // 保存其他配置
    const res = await updateAdminSiteConfig({
      siteName,
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
        <label class="block text-sm font-medium text-gray-700">网站 Logo</label>
        <div class="flex items-center gap-4">
          <!-- Logo 预览 -->
          <div class="w-16 h-16 rounded-lg border border-gray-200 bg-gray-50 overflow-hidden flex items-center justify-center flex-shrink-0">
            <img
              v-if="logoPreviewUrl"
              :src="logoPreviewUrl"
              alt="Logo"
              class="w-full h-full object-cover"
            />
            <span v-else class="text-xs text-gray-400">无 Logo</span>
          </div>
          <!-- 上传按钮 -->
          <label class="inline-flex items-center h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors cursor-pointer">
            <input
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleLogoFile"
            />
            选择图片
          </label>
        </div>
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

