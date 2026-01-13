<script setup>
/**
 * 注册页面组件
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'

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

/**
 * 处理注册提交
 */
const handleSubmit = async () => {
  // 验证密码一致性
  if (form.value.password !== form.value.confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    // TODO: 调用注册接口
    console.log('注册', form.value)
    router.push('/login')
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
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="w-full max-w-md space-y-8">
      <!-- 标题 -->
      <div class="text-center">
        <h1 class="text-3xl font-bold tracking-tight text-gray-900">
          创建账户
        </h1>
        <p class="mt-2 text-sm text-gray-600">
          注册一个新账户
        </p>
      </div>

      <!-- 注册表单卡片 -->
      <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-8">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- 用户名 -->
          <div class="space-y-2">
            <label for="username" class="block text-sm font-medium text-gray-700">
              用户名
            </label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              placeholder="请输入用户名"
              class="w-full h-10 px-3 rounded-md border border-gray-300 bg-white text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:border-transparent transition-colors"
            />
          </div>

          <!-- 邮箱 -->
          <div class="space-y-2">
            <label for="email" class="block text-sm font-medium text-gray-700">
              邮箱
            </label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              placeholder="请输入邮箱"
              class="w-full h-10 px-3 rounded-md border border-gray-300 bg-white text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:border-transparent transition-colors"
            />
          </div>

          <!-- 密码 -->
          <div class="space-y-2">
            <label for="password" class="block text-sm font-medium text-gray-700">
              密码
            </label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              placeholder="请输入密码"
              class="w-full h-10 px-3 rounded-md border border-gray-300 bg-white text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:border-transparent transition-colors"
            />
          </div>

          <!-- 确认密码 -->
          <div class="space-y-2">
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
              确认密码
            </label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              placeholder="请再次输入密码"
              class="w-full h-10 px-3 rounded-md border border-gray-300 bg-white text-sm
                     placeholder:text-gray-400 focus:outline-none focus:ring-2
                     focus:ring-gray-900 focus:border-transparent transition-colors"
            />
          </div>

          <!-- 注册按钮 -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full h-10 px-4 rounded-md bg-gray-900 text-white text-sm font-medium
                   hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-900
                   focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed
                   transition-colors"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <!-- 分隔线 -->
        <div class="mt-6 flex items-center">
          <div class="flex-1 border-t border-gray-200"></div>
          <span class="px-4 text-sm text-gray-500">或</span>
          <div class="flex-1 border-t border-gray-200"></div>
        </div>

        <!-- 登录链接 -->
        <div class="mt-6 text-center">
          <p class="text-sm text-gray-600">
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
  </div>
</template>
