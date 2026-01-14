import request from '@/utils/request'

export function listCategories() {
  return request({
    url: '/knowledge/categories',
    method: 'get'
  })
}

export function listArticles(params = {}) {
  return request({
    url: '/knowledge/articles',
    method: 'get',
    params
  })
}

export function getArticle(id) {
  return request({
    url: `/knowledge/articles/${id}`,
    method: 'get'
  })
}

