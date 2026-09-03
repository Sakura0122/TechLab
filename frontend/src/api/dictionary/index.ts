import request from '@/utils/request'
import type { PageRequest, PageResult } from '@/types/common'
import type {
  Dictionary,
  DictionaryDetail,
  DictionaryInput,
  DictionaryMap,
  DictionaryQuery,
} from './type'

export const dictionaryApi = {
  page: (params: PageRequest<DictionaryQuery>) =>
    request.get<PageResult<Dictionary>>('/dictionary', params),
  detail: (id: string) => request.get<DictionaryDetail>(`/dictionary/${id}`),
  create: (data: DictionaryInput) => request.post<DictionaryDetail>('/dictionary', data),
  update: (id: string, data: DictionaryInput) =>
    request.put<DictionaryDetail>(`/dictionary/${id}`, data),
  delete: (id: string) => request.delete<void>(`/dictionary/${id}`),
  batch: (codes: string[]) => request.post<DictionaryMap>('/dictionary/batch', codes),
}
