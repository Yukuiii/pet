import request from '@/utils/request'

export function latestAnnouncement() {
  return request({
    url: '/announcements/latest',
    method: 'get'
  })
}

export function listAnnouncements(params = {}) {
  return request({
    url: '/announcements',
    method: 'get',
    params
  })
}

