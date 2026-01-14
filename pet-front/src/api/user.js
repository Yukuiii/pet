import request from '@/utils/request'

/**
 * 用户登录
 *
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 登录结果
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 *
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.email - 邮箱
 * @param {string} data.password - 密码
 * @returns {Promise} 注册结果
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getMe() {
  return request({
    url: '/user/me',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/user/me',
    method: 'put',
    data
  })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/user/me/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updatePassword(data) {
  return request({
    url: '/user/me/password',
    method: 'put',
    data
  })
}
