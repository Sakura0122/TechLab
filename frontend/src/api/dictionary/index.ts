import request from '@/utils/request'
import type {
  Dictionary,
  DictionaryDetail,
  DictionaryInput,
  DictionaryPageParams,
  PageResult,
} from './type'

export const dictionaryApi = {
  page: (params: DictionaryPageParams) =>
    request.get<PageResult<Dictionary>>('/dictionary', params),
  detail: (id: string) => request.get<DictionaryDetail>(`/dictionary/${id}`),
  create: (data: DictionaryInput) => request.post<DictionaryDetail>('/dictionary', data),
  update: (id: string, data: DictionaryInput) =>
    request.put<DictionaryDetail>(`/dictionary/${id}`, data),
  delete: (id: string) => request.delete<void>(`/dictionary/${id}`),
}
