<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createPet, deletePet, listMyPets } from '@/api/pet'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const pets = ref([])

const form = ref({
  name: '',
  breed: '',
  gender: '',
  birthday: ''
})

const genderLabel = (gender) => {
  if (gender === 0) return '公'
  if (gender === 1) return '母'
  return '-'
}

const ensureLogin = () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    router.replace('/login')
    return false
  }
  return true
}

const loadPets = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await listMyPets()
    if (res.code === 200) {
      pets.value = res.data || []
    } else {
      errorMsg.value = res.message || '获取宠物列表失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleCreate = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  const name = (form.value.name || '').trim()
  if (!name) {
    errorMsg.value = '宠物姓名不能为空'
    return
  }
  saving.value = true
  try {
    const payload = {
      name,
      breed: form.value.breed || null,
      gender: form.value.gender === '' ? null : Number(form.value.gender),
      birthday: form.value.birthday || null
    }
    const res = await createPet(payload)
    if (res.code === 200) {
      successMsg.value = '已添加宠物档案'
      form.value = { name: '', breed: '', gender: '', birthday: '' }
      await loadPets()
    } else {
      errorMsg.value = res.message || '添加失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    saving.value = false
  }
}

const handleDelete = async (petId) => {
  successMsg.value = ''
  errorMsg.value = ''
  if (!confirm('确认删除该宠物档案？其健康记录也会被删除。')) return
  try {
    const res = await deletePet(petId)
    if (res.code === 200) {
      successMsg.value = '已删除'
      await loadPets()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const toDetail = (petId) => router.push(`/pets/${petId}`)
const toHome = () => router.push('/')

const empty = computed(() => !loading.value && pets.value.length === 0)

onMounted(async () => {
  if (!ensureLogin()) return
  await loadPets()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-5xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">宠物档案</h1>
          <p class="text-sm text-gray-500 mt-1">管理你的宠物信息与健康记录</p>
        </div>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="toHome"
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
        <h2 class="text-lg font-semibold text-gray-900 mb-4">新增宠物</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div class="space-y-1.5 sm:col-span-2">
            <label class="block text-sm font-medium text-gray-700">姓名</label>
            <input
              v-model="form.name"
              type="text"
              placeholder="例如：豆豆"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
          <div class="space-y-1.5">
            <label class="block text-sm font-medium text-gray-700">品种</label>
            <input
              v-model="form.breed"
              type="text"
              placeholder="例如：金毛"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
          <div class="space-y-1.5">
            <label class="block text-sm font-medium text-gray-700">性别</label>
            <select
              v-model="form.gender"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            >
              <option value="">未知</option>
              <option value="0">公</option>
              <option value="1">母</option>
            </select>
          </div>
          <div class="space-y-1.5 sm:col-span-2">
            <label class="block text-sm font-medium text-gray-700">生日</label>
            <input
              v-model="form.birthday"
              type="date"
              class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
            />
          </div>
        </div>
        <div class="mt-5 flex items-center justify-end">
          <button
            class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            :disabled="saving"
            @click="handleCreate"
          >
            {{ saving ? '保存中...' : '添加' }}
          </button>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">我的宠物</h2>
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            :disabled="loading"
            @click="loadPets"
          >
            {{ loading ? '刷新中...' : '刷新' }}
          </button>
        </div>

        <div v-if="empty" class="text-sm text-gray-500">还没有宠物档案，先新增一个吧。</div>

        <div v-else class="space-y-3">
          <div
            v-for="p in pets"
            :key="p.id"
            class="flex items-center justify-between gap-4 p-4 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors"
          >
            <div class="min-w-0">
              <div class="text-sm font-medium text-gray-900 truncate">
                {{ p.name }}
              </div>
              <div class="text-xs text-gray-500 mt-1">
                <span>{{ p.breed || '未知品种' }}</span>
                <span class="mx-2">·</span>
                <span>{{ genderLabel(p.gender) }}</span>
                <span class="mx-2">·</span>
                <span>{{ p.birthday || '未知生日' }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <button
                class="h-10 px-4 rounded-lg bg-gray-900 text-sm text-white hover:bg-gray-800 transition-colors"
                @click="toDetail(p.id)"
              >
                详情
              </button>
              <button
                class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                @click="handleDelete(p.id)"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

