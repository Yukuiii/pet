import { computed, ref } from 'vue'

/**
 * 全局响应式用户状态（模块级单例）
 * 解决 localStorage 非响应式导致跨组件状态不同步的问题
 */
const userState = ref(null)

// 初始化：从 localStorage 恢复用户状态
const stored = localStorage.getItem('user')
if (stored) {
  try {
    userState.value = JSON.parse(stored)
  } catch (e) {
    localStorage.removeItem('user')
  }
}

/**
 * 用户状态 composable
 * @returns {{ user: import('vue').ComputedRef, setUser: Function, clearUser: Function }}
 */
export function useUserStore() {
  /** 当前用户（只读） */
  const user = computed(() => userState.value)

  /**
   * 设置用户信息，同步更新响应式状态和 localStorage
   * @param {object} userData - 用户数据
   */
  const setUser = (userData) => {
    userState.value = userData
    if (userData) {
      localStorage.setItem('user', JSON.stringify(userData))
    } else {
      localStorage.removeItem('user')
    }
  }

  /**
   * 清除用户信息（退出登录）
   */
  const clearUser = () => {
    userState.value = null
    localStorage.removeItem('user')
  }

  return { user, setUser, clearUser }
}
