import request from '@/utils/request'

export function listPosts() {
  return request({
    url: '/community/posts',
    method: 'get'
  })
}

export function createPost(content, files) {
  const formData = new FormData()
  formData.append('content', content)
  if (files && files.length) {
    for (const f of files) {
      formData.append('files', f)
    }
  }
  return request({
    url: '/community/posts',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deletePost(postId) {
  return request({
    url: `/community/posts/${postId}`,
    method: 'delete'
  })
}

export function listComments(postId) {
  return request({
    url: `/community/posts/${postId}/comments`,
    method: 'get'
  })
}

export function createComment(postId, data) {
  return request({
    url: `/community/posts/${postId}/comments`,
    method: 'post',
    data
  })
}

export function deleteComment(postId, commentId) {
  return request({
    url: `/community/posts/${postId}/comments/${commentId}`,
    method: 'delete'
  })
}

