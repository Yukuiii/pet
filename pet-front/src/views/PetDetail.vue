<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createHealthRecord,
  deleteHealthRecord,
  listHealthRecords,
  listMyPets,
  updateHealthRecord,
  updatePet,
  uploadPetPhoto
} from '@/api/pet'
import { getMediaUrl } from '@/utils/url'

const route = useRoute()
const router = useRouter()

const petId = computed(() => Number(route.params.id))

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const pet = ref(null)
const form = ref({
  name: '',
  breed: '',
  gender: '',
  birthday: '',
  photo: ''
})

const recordsLoading = ref(false)
const records = ref([])
const editingRecordId = ref(null)
const recordForm = ref({
  recordTime: '',
  title: '',
  content: ''
})

const ensureLogin = () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    router.replace('/login')
    return false
  }
  return true
}

const photoUrl = computed(() => {
  return getMediaUrl(pet.value?.photo)
})

const genderLabel = (gender) => {
  if (gender === 0) return '公'
  if (gender === 1) return '母'
  return '未知'
}

const loadPet = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await listMyPets()
    if (res.code === 200) {
      const found = (res.data || []).find((p) => p.id === petId.value)
      if (!found) {
        errorMsg.value = '宠物不存在或无权限'
        return
      }
      pet.value = found
      form.value = {
        name: found.name || '',
        breed: found.breed || '',
        gender: found.gender === null || found.gender === undefined ? '' : String(found.gender),
        birthday: found.birthday || '',
        photo: found.photo || ''
      }
    } else {
      errorMsg.value = res.message || '获取宠物信息失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const loadRecords = async () => {
  recordsLoading.value = true
  errorMsg.value = ''
  try {
    const res = await listHealthRecords(petId.value)
    if (res.code === 200) {
      records.value = res.data || []
    } else {
      errorMsg.value = res.message || '获取健康记录失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    recordsLoading.value = false
  }
}

const handleSave = async () => {
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
      birthday: form.value.birthday || null,
      photo: form.value.photo || null
    }
    const res = await updatePet(petId.value, payload)
    if (res.code === 200) {
      pet.value = res.data
      successMsg.value = '已保存'
      await loadPet()
    } else {
      errorMsg.value = res.message || '保存失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    saving.value = false
  }
}

const handlePhotoChange = async (event) => {
  successMsg.value = ''
  errorMsg.value = ''
  const file = event.target.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const res = await uploadPetPhoto(petId.value, file)
    if (res.code === 200) {
      pet.value = res.data
      form.value.photo = res.data.photo || ''
      successMsg.value = '照片已更新'
    } else {
      errorMsg.value = res.message || '上传失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

const handleAddRecord = async () => {
  successMsg.value = ''
  errorMsg.value = ''
  const title = (recordForm.value.title || '').trim()
  if (!title) {
    errorMsg.value = '标题不能为空'
    return
  }
  try {
    const payload = {
      recordTime: recordForm.value.recordTime ? recordForm.value.recordTime : null,
      title,
      content: recordForm.value.content || null
    }
    const res = editingRecordId.value
      ? await updateHealthRecord(petId.value, editingRecordId.value, payload)
      : await createHealthRecord(petId.value, payload)
    if (res.code === 200) {
      successMsg.value = editingRecordId.value ? '已更新健康记录' : '已添加健康记录'
      editingRecordId.value = null
      recordForm.value = { recordTime: '', title: '', content: '' }
      await loadRecords()
    } else {
      errorMsg.value = res.message || (editingRecordId.value ? '更新失败' : '添加失败')
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const handleEditRecord = (r) => {
  successMsg.value = ''
  errorMsg.value = ''
  editingRecordId.value = r.id
  recordForm.value = {
    recordTime: r.recordTime || '',
    title: r.title || '',
    content: r.content || ''
  }
}

const cancelEditRecord = () => {
  editingRecordId.value = null
  recordForm.value = { recordTime: '', title: '', content: '' }
}

const handleDeleteRecord = async (recordId) => {
  successMsg.value = ''
  errorMsg.value = ''
  if (!confirm('确认删除该条健康记录？')) return
  try {
    const res = await deleteHealthRecord(petId.value, recordId)
    if (res.code === 200) {
      successMsg.value = '已删除'
      if (editingRecordId.value === recordId) {
        editingRecordId.value = null
        recordForm.value = { recordTime: '', title: '', content: '' }
      }
      await loadRecords()
    } else {
      errorMsg.value = res.message || '删除失败'
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  }
}

const toList = () => router.push('/pets')

onMounted(async () => {
  if (!ensureLogin()) return
  await loadPet()
  await loadRecords()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-5xl mx-auto px-6 py-10">
      <div class="flex items-center justify-between mb-8">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900">宠物详情</h1>
          <p class="text-sm text-gray-500 mt-1" v-if="pet">
            {{ pet.name }} · {{ pet.breed || '未知品种' }} · {{ genderLabel(pet.gender) }}
          </p>
        </div>
        <button
          class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
          @click="toList"
        >
          返回列表
        </button>
      </div>

      <div v-if="errorMsg" class="mb-4 p-3 rounded-lg bg-red-50 text-red-600 text-sm">
        {{ errorMsg }}
      </div>
      <div v-if="successMsg" class="mb-4 p-3 rounded-lg bg-green-50 text-green-700 text-sm">
        {{ successMsg }}
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6 mb-6">
        <div class="flex items-start gap-6">
          <div class="w-28">
            <div class="w-28 h-28 rounded-2xl bg-gray-100 border border-gray-200 overflow-hidden flex items-center justify-center">
              <img v-if="photoUrl" :src="photoUrl" alt="pet" class="w-full h-full object-cover" />
              <div v-else class="text-gray-400 text-sm">无照片</div>
            </div>
            <label
              class="mt-3 inline-flex w-full items-center justify-center h-10 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors cursor-pointer"
            >
              <input type="file" accept="image/*" class="hidden" :disabled="uploading" @change="handlePhotoChange" />
              {{ uploading ? '上传中...' : '更换照片' }}
            </label>
          </div>

          <div class="flex-1">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="space-y-1.5 sm:col-span-2">
                <label class="block text-sm font-medium text-gray-700">姓名</label>
                <input
                  v-model="form.name"
                  type="text"
                  class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-gray-50 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:bg-white focus:border-transparent transition-all"
                />
              </div>
              <div class="space-y-1.5">
                <label class="block text-sm font-medium text-gray-700">品种</label>
                <input
                  v-model="form.breed"
                  type="text"
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
                :disabled="saving || loading"
                @click="handleSave"
              >
                {{ saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white border border-gray-200 rounded-xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">健康记录</h2>
          <button
            class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
            :disabled="recordsLoading"
            @click="loadRecords"
          >
            {{ recordsLoading ? '刷新中...' : '刷新' }}
          </button>
        </div>

        <div class="rounded-lg border border-gray-200 bg-gray-50 p-4 mb-5">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="block text-sm font-medium text-gray-700">记录时间</label>
              <input
                v-model="recordForm.recordTime"
                type="datetime-local"
                class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent transition-all"
              />
            </div>
            <div class="space-y-1.5">
              <label class="block text-sm font-medium text-gray-700">标题 / 疫苗名称</label>
              <input
                v-model="recordForm.title"
                type="text"
                placeholder="例如：狂犬疫苗"
                class="w-full h-11 px-4 rounded-lg border border-gray-200 bg-white text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent transition-all"
              />
            </div>
            <div class="space-y-1.5 sm:col-span-2">
              <label class="block text-sm font-medium text-gray-700">内容 / 备注</label>
              <textarea
                v-model="recordForm.content"
                rows="3"
                placeholder="可选"
                class="w-full px-4 py-3 rounded-lg border border-gray-200 bg-white text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent transition-all"
              ></textarea>
            </div>
          </div>
          <div class="mt-4 flex items-center justify-end gap-2">
            <button
              v-if="editingRecordId"
              class="h-11 px-5 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
              @click="cancelEditRecord"
            >
              取消编辑
            </button>
            <button
              class="h-11 px-5 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-all"
              @click="handleAddRecord"
            >
              {{ editingRecordId ? '保存修改' : '添加记录' }}
            </button>
          </div>
        </div>

        <div v-if="!recordsLoading && records.length === 0" class="text-sm text-gray-500">
          暂无健康记录。
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="r in records"
            :key="r.id"
            class="p-4 rounded-lg border border-gray-200"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <div class="text-sm font-medium text-gray-900 truncate">{{ r.title }}</div>
                <div class="text-xs text-gray-500 mt-1">{{ r.recordTime }}</div>
                <div v-if="r.content" class="text-sm text-gray-700 mt-3 whitespace-pre-wrap">{{ r.content }}</div>
              </div>
              <div class="flex items-center gap-2">
                <button
                  class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="handleEditRecord(r)"
                >
                  编辑
                </button>
                <button
                  class="h-10 px-4 rounded-lg bg-white border border-gray-200 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                  @click="handleDeleteRecord(r.id)"
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
</template>
