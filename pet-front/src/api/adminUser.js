import request from '@/utils/request'

export function pageUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function updateUserRole(id, role) {
  return request({
    url: `/admin/users/${id}/role`,
    method: 'put',
    data: { role }
  })
}
