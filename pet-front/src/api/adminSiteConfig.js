import request from '@/utils/request'

/**
 * 获取网站配置
 */
export function getAdminSiteConfig() {
  return request({
    url: '/admin/site-config',
    method: 'get'
  })
}

/**
 * 更新网站配置
 * @param {Object} data 配置数据
 */
export function updateAdminSiteConfig(data) {
  return request({
    url: '/admin/site-config',
    method: 'put',
    data
  })
}

/**
 * 上传网站 Logo
 * @param {File} file Logo 图片文件
 */
export function uploadSiteConfigLogo(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/site-config/logo',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

