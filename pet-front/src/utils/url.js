/**
 * 获取静态资源基础 URL
 * @returns {string} 静态资源基础 URL
 */
export const getStaticBaseUrl = () => {
  return import.meta.env.VITE_STATIC_BASE_URL || ''
}

/**
 * 获取完整的媒体资源 URL
 * @param {string} url - 资源路径
 * @returns {string} 完整的资源 URL
 */
export const getMediaUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return `${getStaticBaseUrl()}${url}`
}
