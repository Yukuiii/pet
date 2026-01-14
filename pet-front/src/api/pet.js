import request from '@/utils/request'

export function listMyPets() {
  return request({
    url: '/pet',
    method: 'get'
  })
}

export function createPet(data) {
  return request({
    url: '/pet',
    method: 'post',
    data
  })
}

export function updatePet(petId, data) {
  return request({
    url: `/pet/${petId}`,
    method: 'put',
    data
  })
}

export function deletePet(petId) {
  return request({
    url: `/pet/${petId}`,
    method: 'delete'
  })
}

export function uploadPetPhoto(petId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/pet/${petId}/photo`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function listHealthRecords(petId) {
  return request({
    url: `/pet/${petId}/records`,
    method: 'get'
  })
}

export function createHealthRecord(petId, data) {
  return request({
    url: `/pet/${petId}/records`,
    method: 'post',
    data
  })
}

export function updateHealthRecord(petId, recordId, data) {
  return request({
    url: `/pet/${petId}/records/${recordId}`,
    method: 'put',
    data
  })
}

export function deleteHealthRecord(petId, recordId) {
  return request({
    url: `/pet/${petId}/records/${recordId}`,
    method: 'delete'
  })
}

