<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMe, updatePassword, updateProfile, uploadAvatar } from '@/api/user'
import { getMediaUrl } from '@/utils/url'

const router = useRouter()

const loading = ref(false)
const savingProfile = ref(false)
const savingPassword = ref(false)
const uploadingAvatar = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const user = ref(null)
const form = ref({
  nickname: ''
})

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const avatarUrl = computed(() => {
  return getMediaUrl(user.value?.avatar)
})

const loadMe = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getMe()
    if (res.code === 200) {
      user.value = res.data
      form.value.nickname = res.data?.nickname || ''
      localStorage.setItem('user', JSON.stringify(res.data))
    } else {
      errorMsg.value = res.message || '获取用户信息失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const ensureLogin = () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    router.replace('/login')
    return false
  }
  try {
    user.value = JSON.parse(userStr)
    form.value.nickname = user.value?.nickname || ''
    return true
  } catch (e) {
    localStorage.removeItem('user')
    router.replace('/login')
    return false
  }
}

const handleSaveProfile = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  const nickname = (form.value.nickname || '').trim()
  if (!nickname) {
    errorMsg.value = '昵称不能为空'
    return
  }
  savingProfile.value = true
  try {
    const res = await updateProfile({ nickname })
    if (res.code === 200) {
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
      successMsg.value = '个人信息已更新'
    } else {
      errorMsg.value = res.message || '更新失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    savingProfile.value = false
  }
}

const handleAvatarChange = async (event) => {
  successMsg.value = ''
  errorMsg.value = ''
  const file = event.target.files?.[0]
  if (!file) return
  uploadingAvatar.value = true
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      user.value = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
      successMsg.value = '头像已更新'
    } else {
      errorMsg.value = res.message || '头像上传失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    uploadingAvatar.value = false
    event.target.value = ''
  }
}

const handleSavePassword = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  const oldPassword = pwdForm.value.oldPassword || ''
  const newPassword = pwdForm.value.newPassword || ''
  const confirmNewPassword = pwdForm.value.confirmNewPassword || ''

  if (!oldPassword.trim()) {
    errorMsg.value = '旧密码不能为空'
    return
  }
  if (!newPassword.trim()) {
    errorMsg.value = '新密码不能为空'
    return
  }
  if (newPassword.length < 6) {
    errorMsg.value = '新密码长度至少 6 位'
    return
  }
  if (newPassword !== confirmNewPassword) {
    errorMsg.value = '两次输入的新密码不一致'
    return
  }

  savingPassword.value = true
  try {
    const res = await updatePassword({ oldPassword, newPassword })
    if (res.code === 200) {
      pwdForm.value.oldPassword = ''
      pwdForm.value.newPassword = ''
      pwdForm.value.confirmNewPassword = ''
      successMsg.value = '密码已更新'
    } else {
      errorMsg.value = res.message || '更新失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    savingPassword.value = false
  }
}

const logout = () => {
  localStorage.removeItem('user')
  router.replace('/login')
}

onMounted(async () => {
  if (!ensureLogin()) return
  await loadMe()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-3xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">个人中心</h1>
          <p class="text-sm text-gray-500 mt-1">管理昵称、头像与密码</p>
        </div>
        <div class="flex items-center gap-3">
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            @click="router.push('/')"
          >
            返回首页
          </button>
          <button
            class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
            @click="logout"
          >
            退出登录
          </button>
        </div>
      </div>

      <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
        {{ errorMsg }}
      </div>
      <div v-if="successMsg" class="mb-4 p-3 rounded-lg bg-green-50 text-green-700 text-sm">
        {{ successMsg }}
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-start gap-6">
          <div class="w-24">
            <div class="w-24 h-24 rounded-2xl bg-gray-100 border border-gray-200 overflow-hidden flex items-center justify-center">
              <img v-if="avatarUrl" :src="avatarUrl" alt="avatar" class="w-full h-full object-cover" />
              <div v-else class="text-gray-400 text-sm">无头像</div>
            </div>
            <label
              class="mt-3 inline-flex w-full items-center justify-center h-10 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors cursor-pointer"
            >
              <input type="file" accept="image/*" class="hidden" :disabled="uploadingAvatar" @change="handleAvatarChange" />
              {{ uploadingAvatar ? '上传中...' : '更换头像' }}
            </label>
          </div>

          <div class="flex-1">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="space-y-1.5">
                <div class="text-sm font-medium text-gray-700">用户名</div>
                <div class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm flex items-center text-gray-700">
                  {{ user?.username || '-' }}
                </div>
              </div>
              <div class="space-y-1.5">
                <div class="text-sm font-medium text-gray-700">邮箱</div>
                <div class="h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm flex items-center text-gray-700">
                  {{ user?.email || '-' }}
                </div>
              </div>
              <div class="space-y-1.5 sm:col-span-2">
                <label for="nickname" class="block text-sm font-medium text-gray-700">昵称</label>
                <input
                  id="nickname"
                  v-model="form.nickname"
                  type="text"
                  placeholder="请输入昵称"
                  class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
                />
              </div>
            </div>

            <div class="mt-5 flex items-center justify-end">
              <button
                class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                :disabled="savingProfile || loading"
                @click="handleSaveProfile"
              >
                {{ savingProfile ? '保存中...' : '保存个人信息' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-6 bg-white border border-gray-200 rounded-xl p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">修改密码</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="space-y-1.5 sm:col-span-2">
            <label for="oldPassword" class="block text-sm font-medium text-gray-700">旧密码</label>
            <input
              id="oldPassword"
              v-model="pwdForm.oldPassword"
              type="password"
              placeholder="请输入旧密码"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
          <div class="space-y-1.5">
            <label for="newPassword" class="block text-sm font-medium text-gray-700">新密码</label>
            <input
              id="newPassword"
              v-model="pwdForm.newPassword"
              type="password"
              placeholder="至少 6 位"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
          <div class="space-y-1.5">
            <label for="confirmNewPassword" class="block text-sm font-medium text-gray-700">确认新密码</label>
            <input
              id="confirmNewPassword"
              v-model="pwdForm.confirmNewPassword"
              type="password"
              placeholder="再输入一次新密码"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
        </div>

        <div class="mt-5 flex items-center justify-end">
          <button
            class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            :disabled="savingPassword || loading"
            @click="handleSavePassword"
          >
            {{ savingPassword ? '保存中...' : '保存新密码' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

