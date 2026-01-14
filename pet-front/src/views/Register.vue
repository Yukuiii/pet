<script setup>
/**
 * 注册页面组件
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

const router = useRouter()

/** 表单数据 */
const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

/** 加载状态 */
const loading = ref(false)

/** 错误信息 */
const errorMsg = ref('')

/**
 * 处理注册提交
 */
const handleSubmit = async () => {
  errorMsg.value = ''

  // 验证密码一致性
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    const res = await register({
      username: form.value.username,
      email: form.value.email,
      password: form.value.password
    })
    if (res.code === 200) {
      router.push('/login')
    } else {
      errorMsg.value = res.message || '注册失败'
    }
  } catch (error) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到登录页面
 */
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen flex">
    <!-- 左侧图片区域 -->
    <div class="hidden lg:flex lg:w-1/2 relative">
      <img
        src="https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=1200"
        alt="宠物"
        class="absolute inset-0 w-full h-full object-cover"
      />
      <!-- 遮罩层 -->
      <div class="absolute inset-0 bg-black/30"></div>
      <!-- 文字内容 -->
      <div class="relative z-10 flex flex-col justify-end p-12 text-white">
        <h2 class="text-4xl font-bold mb-4">加入我们</h2>
        <p class="text-lg text-white/80">开启您的宠物管理之旅</p>
      </div>
    </div>

    <!-- 右侧注册区域 -->
    <div class="w-full lg:w-1/2 flex items-center justify-center bg-white px-6 py-12">
      <div class="w-full max-w-sm">
        <!-- 标题 -->
        <div class="mb-8">
          <h1 class="text-2xl font-semibold text-gray-900">
            创建账户
          </h1>
          <p class="mt-1 text-sm text-gray-500">
            注册一个新账户开始使用
          </p>
        </div>

        <!-- 注册表单 -->
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- 错误提示 -->
          <div v-if="errorMsg" class="p-3 rounded-lg bg-red-50 text-red-600 text-sm">
            {{ errorMsg }}
          </div>

          <!-- 用户名 -->
          <div class="space-y-1.5">
            <label for="username" class="block text-sm font-medium text-gray-700">
              用户名
            </label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              placeholder="请输入用户名"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>

          <!-- 邮箱 -->
          <div class="space-y-1.5">
            <label for="email" class="block text-sm font-medium text-gray-700">
              邮箱
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              placeholder="请输入邮箱"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>

          <!-- 密码 -->
          <div class="space-y-1.5">
            <label for="password" class="block text-sm font-medium text-gray-700">
              密码
            </label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              placeholder="请输入密码"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>

          <!-- 确认密码 -->
          <div class="space-y-1.5">
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
              确认密码
            </label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              placeholder="请再次输入密码"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>

          <!-- 注册按钮 -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full h-11 mt-2 rounded-lg bg-gray-900 text-white text-sm font-medium
                   hover:bg-gray-800 active:scale-[0.98] focus:outline-none focus:ring-2
                   focus:ring-gray-900 focus:ring-offset-2 disabled:opacity-50
                   disabled:cursor-not-allowed transition-all"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <!-- 登录链接 -->
        <p class="mt-8 text-center text-sm text-gray-500">
          已有账户？
          <button
            @click="goToLogin"
            class="font-medium text-gray-900 hover:underline"
          >
            立即登录
          </button>
        </p>
      </div>
    </div>
  </div>
</template>
