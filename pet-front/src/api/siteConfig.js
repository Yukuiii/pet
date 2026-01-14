import request from '@/utils/request'

export function getSiteConfig() {
  return request({
    url: '/site-config',
    method: 'get'
  })
}

