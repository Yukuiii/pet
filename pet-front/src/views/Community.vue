<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createComment, createPost, deleteComment, deletePost, listComments, listPosts } from '@/api/community'
import { getMediaUrl } from '@/utils/url'

const router = useRouter()

const loading = ref(false)
const posting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const posts = ref([])
const expanded = ref(new Set())
const commentsByPostId = ref({})
const commentsLoadingByPostId = ref({})
const commentDraftByPostId = ref({})

const form = ref({
  content: ''
})
const selectedFiles = ref([])

const me = computed(() => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return null
  try {
    return JSON.parse(userStr)
  } catch (e) {
    return null
  }
})

const ensureLogin = () => {
  if (!me.value) {
    router.replace('/login')
    return false
  }
  return true
}

const loadPosts = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await listPosts()
    if (res.code === 200) {
      posts.value = res.data || []
    } else {
      errorMsg.value = res.message || '获取动态列表失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleFiles = (event) => {
  const files = Array.from(event.target.files || [])
  selectedFiles.value = files
}

const handlePublish = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  const content = (form.value.content || '').trim()
  if (!content) {
    errorMsg.value = '动态内容不能为空'
    return
  }
  posting.value = true
  try {
    const res = await createPost(content, selectedFiles.value)
    if (res.code === 200) {
      successMsg.value = '发布成功'
      form.value.content = ''
      selectedFiles.value = []
      await loadPosts()
    } else {
      errorMsg.value = res.message || '发布失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    posting.value = false
  }
}

const handleDeletePost = async (postId) => {
  successMsg.value = ''
  errorMsg.value = ''
  if (!confirm('确认删除该动态？')) return
  try {
    const res = await deletePost(postId)
    if (res.code === 200) {
      successMsg.value = '已删除'
      await loadPosts()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const loadPostComments = async (postId) => {
  commentsLoadingByPostId.value = { ...commentsLoadingByPostId.value, [postId]: true }
  try {
    const res = await listComments(postId)
    if (res.code === 200) {
      commentsByPostId.value = { ...commentsByPostId.value, [postId]: res.data || [] }
    } else {
      errorMsg.value = res.message || '获取评论失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    commentsLoadingByPostId.value = { ...commentsLoadingByPostId.value, [postId]: false }
  }
}

const toggleComments = async (postId) => {
  const next = new Set(expanded.value)
  if (next.has(postId)) {
    next.delete(postId)
    expanded.value = next
    return
  }
  next.add(postId)
  expanded.value = next
  if (!commentsByPostId.value[postId]) {
    await loadPostComments(postId)
  }
}

const handleAddComment = async (postId) => {
  successMsg.value = ''
  errorMsg.value = ''
  const content = (commentDraftByPostId.value[postId] || '').trim()
  if (!content) {
    errorMsg.value = '评论内容不能为空'
    return
  }
  try {
    const res = await createComment(postId, { content })
    if (res.code === 200) {
      commentDraftByPostId.value = { ...commentDraftByPostId.value, [postId]: '' }
      await loadPostComments(postId)
    } else {
      errorMsg.value = res.message || '评论失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const handleDeleteComment = async (postId, commentId) => {
  successMsg.value = ''
  errorMsg.value = ''
  if (!confirm('确认删除该评论？')) return
  try {
    const res = await deleteComment(postId, commentId)
    if (res.code === 200) {
      await loadPostComments(postId)
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

onMounted(async () => {
  if (!ensureLogin()) return
  await loadPosts()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-4xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">社区动态</h1>
          <p class="text-sm text-gray-500 mt-1">分享宠物日常与心得</p>
        </div>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="router.push('/')"
        >
          返回首页
        </button>
      </div>

      <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
        {{ errorMsg }}
      </div>
      <div v-if="successMsg" class="mb-4 p-3 rounded-lg bg-green-50 text-green-700 text-sm">
        {{ successMsg }}
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">发布动态</h2>
        <div class="space-y-4">
          <textarea
            v-model="form.content"
            rows="4"
            placeholder="说点什么吧..."
            class="w-full px-4 py-3 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
          ></textarea>

          <div class="flex items-center justify-between gap-4">
            <label class="inline-flex items-center h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors cursor-pointer">
              <input
                type="file"
                multiple
                accept="image/*"
                class="hidden"
                @change="handleFiles"
              />
              选择图片
            </label>
            <div class="text-xs text-gray-500">
              已选择 {{ selectedFiles.length }} 张
            </div>
          </div>

          <div class="flex items-center justify-end">
            <button
              class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              :disabled="posting"
              @click="handlePublish"
            >
              {{ posting ? '发布中...' : '发布' }}
            </button>
          </div>
        </div>
      </div>

      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-gray-900">最新动态</h2>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          :disabled="loading"
          @click="loadPosts"
        >
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
      </div>

      <div v-if="!loading && posts.length === 0" class="text-sm text-gray-500">
        暂无动态，快来发布第一条吧。
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="p in posts"
          :key="p.id"
          class="bg-white border border-gray-200 rounded-xl p-6"
        >
          <div class="flex items-start justify-between gap-4">
            <div class="flex items-center gap-3 min-w-0">
              <div class="w-10 h-10 rounded-full bg-gray-100 border border-gray-200 overflow-hidden flex items-center justify-center">
                <img v-if="p.author?.avatar" :src="getMediaUrl(p.author.avatar)" alt="avatar" class="w-full h-full object-cover" />
                <div v-else class="text-xs text-gray-400">头像</div>
              </div>
              <div class="min-w-0">
                <div class="text-sm font-medium text-gray-900 truncate">
                  {{ p.author?.nickname || '用户' }}
                </div>
                <div class="text-xs text-gray-500">
                  {{ p.createTime }}
                </div>
              </div>
            </div>

            <button
              v-if="me && p.author?.id === me.id"
              class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
              @click="handleDeletePost(p.id)"
            >
              删除
            </button>
          </div>

          <div class="mt-4 text-sm text-gray-800 whitespace-pre-wrap">
            {{ p.content }}
          </div>

          <div v-if="p.images && p.images.length" class="mt-4 grid grid-cols-2 sm:grid-cols-3 gap-3">
            <img
              v-for="(img, idx) in p.images"
              :key="idx"
              :src="getMediaUrl(img)"
              alt="img"
              class="w-full aspect-square object-cover rounded-lg border border-gray-200"
            />
          </div>

          <div class="mt-5 flex items-center justify-between">
            <button
              class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
              @click="toggleComments(p.id)"
            >
              {{ expanded.has(p.id) ? '收起评论' : '查看评论' }}
            </button>
          </div>

          <div v-if="expanded.has(p.id)" class="mt-4 border-t border-gray-100 pt-4 space-y-3">
            <div class="flex items-center gap-3">
              <input
                v-model="commentDraftByPostId[p.id]"
                type="text"
                placeholder="写下你的评论..."
                class="flex-1 h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
              />
              <button
                class="h-11 px-4 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-all"
                @click="handleAddComment(p.id)"
              >
                发送
              </button>
            </div>

            <div v-if="commentsLoadingByPostId[p.id]" class="text-sm text-gray-500">加载中...</div>
            <div v-else-if="(commentsByPostId[p.id] || []).length === 0" class="text-sm text-gray-500">暂无评论</div>
            <div v-else class="space-y-3">
              <div
                v-for="c in commentsByPostId[p.id]"
                :key="c.id"
                class="p-4 rounded-lg border border-gray-200"
              >
                <div class="flex items-start justify-between gap-4">
                  <div class="min-w-0">
                    <div class="text-sm font-medium text-gray-900">
                      {{ c.author?.nickname || '用户' }}
                      <span class="ml-2 text-xs text-gray-500">{{ c.createTime }}</span>
                    </div>
                    <div class="text-sm text-gray-800 mt-2 whitespace-pre-wrap">{{ c.content }}</div>
                  </div>
                  <button
                    v-if="me && c.author?.id === me.id"
                    class="h-9 px-3 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    @click="handleDeleteComment(p.id, c.id)"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

