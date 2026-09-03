import type { DictionaryType } from '@/constants/dictionary'

export interface DictionaryQuery {
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

export type DictionaryMap = Record<string, DictionaryItem[]>

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
