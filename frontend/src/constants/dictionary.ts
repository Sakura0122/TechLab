export const DICTIONARY_TYPE = {
  SYSTEM: 1,
  BUSINESS: 2,
} as const

export const DICTIONARY_TYPE_FILTER_ALL = 0 as const

export type DictionaryType = (typeof DICTIONARY_TYPE)[keyof typeof DICTIONARY_TYPE]

export const DICTIONARY_TYPE_OPTIONS: ReadonlyArray<{
  label: string
  value: DictionaryType
}> = [
  { label: '系统字典', value: DICTIONARY_TYPE.SYSTEM },
  { label: '业务字典', value: DICTIONARY_TYPE.BUSINESS },
]

export const DICTIONARY_TYPE_LABELS: Record<DictionaryType, string> = {
  [DICTIONARY_TYPE.SYSTEM]: '系统字典',
  [DICTIONARY_TYPE.BUSINESS]: '业务字典',
}
