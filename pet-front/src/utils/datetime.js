/**
 * 解析后端返回的日期时间
 * 兼容无时区字符串（如 2026-01-15T18:10:17）与标准 ISO 字符串
 * @param {string|number|Date} value - 原始时间值
 * @returns {Date|null} 解析后的 Date 对象
 */
export const parseDateTime = (value) => {
  if (!value) return null
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value
  }
  if (typeof value === 'number') {
    const date = new Date(value)
    return Number.isNaN(date.getTime()) ? null : date
  }

  const timeStr = String(value).trim()
  if (!timeStr) return null

  const normalized = timeStr.replace('T', ' ')
  const localMatch = normalized.match(
    /^(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2})(?::(\d{2}))?(?:\.\d+)?$/
  )

  if (localMatch) {
    const [, year, month, day, hour, minute, second = '0'] = localMatch
    return new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute),
      Number(second)
    )
  }

  const date = new Date(timeStr)
  return Number.isNaN(date.getTime()) ? null : date
}

/**
 * 格式化日期时间（精确到分钟）
 * @param {string|number|Date} value - 原始时间值
 * @returns {string} 格式化后的时间字符串
 */
export const formatDateTime = (value) => {
  const date = parseDateTime(value)
  if (!date) return value ? String(value) : ''

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}
