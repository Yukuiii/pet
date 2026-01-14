import request from '@/utils/request'

export function pageAnnouncements(params) {
  return request({
    url: '/admin/announcements',
    method: 'get',
    params
  })
}

export function createAnnouncement(data) {
  return request({
    url: '/admin/announcements',
    method: 'post',
    data
  })
}

export function updateAnnouncementStatus(id, status) {
  return request({
    url: `/admin/announcements/${id}/status`,
    method: 'put',
    data: { status }
  })
}

