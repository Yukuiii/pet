import axios from 'axios'

/**
 * 创建 axios 实例
 */
const request = axios.create({
  baseURL: 'http://129.204.27.16:8910/api',
  timeout: 10000
})

/**
 * 请求拦截器
 */
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取用户信息，添加到请求头
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      config.headers['X-User-Id'] = user.id
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default request
