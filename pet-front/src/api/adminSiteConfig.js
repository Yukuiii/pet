import request from '@/utils/request'

export function getAdminSiteConfig() {
  return request({
    url: '/admin/site-config',
    method: 'get'
  })
}

export function updateAdminSiteConfig(data) {
  return request({
    url: '/admin/site-config',
    method: 'put',
    data
  })
}

