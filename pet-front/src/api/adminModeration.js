import request from '@/utils/request'

export function pagePosts(params) {
  return request({
    url: '/admin/moderation/posts',
    method: 'get',
    params
  })
}

export function deletePost(postId) {
  return request({
    url: `/admin/moderation/posts/${postId}`,
    method: 'delete'
  })
}

export function pageComments(params) {
  return request({
    url: '/admin/moderation/comments',
    method: 'get',
    params
  })
}

export function deleteComment(commentId) {
  return request({
    url: `/admin/moderation/comments/${commentId}`,
    method: 'delete'
  })
}

