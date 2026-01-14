import request from '@/utils/request'

export function adminListKnowledgeCategories(params) {
  return request({
    url: '/admin/knowledge/categories',
    method: 'get',
    params
  })
}

export function adminCreateKnowledgeCategory(data) {
  return request({
    url: '/admin/knowledge/categories',
    method: 'post',
    data
  })
}

export function adminUpdateKnowledgeCategory(id, data) {
  return request({
    url: `/admin/knowledge/categories/${id}`,
    method: 'put',
    data
  })
}

export function adminDeleteKnowledgeCategory(id) {
  return request({
    url: `/admin/knowledge/categories/${id}`,
    method: 'delete'
  })
}

export function adminPageKnowledgeArticles(params) {
  return request({
    url: '/admin/knowledge/articles',
    method: 'get',
    params
  })
}

export function adminGetKnowledgeArticle(id) {
  return request({
    url: `/admin/knowledge/articles/${id}`,
    method: 'get'
  })
}

export function adminCreateKnowledgeArticle(data) {
  return request({
    url: '/admin/knowledge/articles',
    method: 'post',
    data
  })
}

export function adminUpdateKnowledgeArticle(id, data) {
  return request({
    url: `/admin/knowledge/articles/${id}`,
    method: 'put',
    data
  })
}

export function adminDeleteKnowledgeArticle(id) {
  return request({
    url: `/admin/knowledge/articles/${id}`,
    method: 'delete'
  })
}

