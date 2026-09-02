import type { DictionaryType } from '@/constants/dictionary'

export interface PageResult<T> {
  total: number
  pageCount: number
  list: T[]
}

export interface DictionaryPageParams {
  currentPage: number
  pageSize: number
  keyword?: string
  type?: DictionaryType
}

export interface DictionaryItem {
  id: string
  name: string
  code: string
  description?: string
  status: 0 | 1
  sort: number
}

export interface Dictionary {
  id: string
  name: string
  code: string
  type: DictionaryType
  description?: string
  createdAt: string
  updatedAt: string
}

export interface DictionaryDetail extends Dictionary {
  items: DictionaryItem[]
}

export interface DictionaryItemInput {
  name: string
  code: string
  description: string
  status: 0 | 1
  sort: number
}

export interface DictionaryInput {
  name: string
  code: string
  type: DictionaryType
  description: string
  items: DictionaryItemInput[]
}
