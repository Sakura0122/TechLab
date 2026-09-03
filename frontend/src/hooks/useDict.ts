import { dictionaryApi } from '@/api/dictionary'
import type { DictionaryMap } from '@/api/dictionary/type'
import { ref } from 'vue'

export const useDict = (codes: string[]) => {
  const dict = ref<DictionaryMap>({})
  const loading = ref(false)

  const load = async () => {
    const normalizedCodes = [...new Set(codes.map((code) => code.trim()).filter(Boolean))]
    if (normalizedCodes.length === 0) {
      dict.value = {}
      return
    }

    loading.value = true
    try {
      const { data } = await dictionaryApi.batch(normalizedCodes)
      dict.value = Object.fromEntries(
        normalizedCodes.map((code) => [code, data[code] ?? []]),
      )
    } catch {
      dict.value = Object.fromEntries(normalizedCodes.map((code) => [code, []]))
    } finally {
      loading.value = false
    }
  }

  void load()

  return {
    dict,
    loading,
    load,
  }
}
