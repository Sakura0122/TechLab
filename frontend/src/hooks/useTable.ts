import type { PageRequest, PageResult } from '@/types/common'
import { nextTick, onMounted, ref, toValue, watch, type MaybeRefOrGetter, type Ref } from 'vue'

export interface TablePagination {
  currentPage: number
  pageSize: number
  total: number
}

interface UseTableOptions<TQuery extends object, R> {
  defaultPageSize?: number
  params?: MaybeRefOrGetter<TQuery>
  formatParams?: (params: PageRequest<TQuery>) => PageRequest<TQuery>
  formatResult?: (data: PageResult<R>) => { list: R[]; total: number }
  onSuccess?: (data: PageResult<R>) => void
  immediate?: boolean
}

interface UseTableReturn<TQuery extends object, R> {
  list: Ref<R[]>
  loading: Ref<boolean>
  pagination: Ref<TablePagination>
  getList: (newParams?: TQuery) => Promise<void>
  search: () => Promise<void>
}

export const useTable = <TQuery extends object, R>(
  api: (params: PageRequest<TQuery>) => Promise<{ data: PageResult<R> }>,
  options: UseTableOptions<TQuery, R> = {},
): UseTableReturn<TQuery, R> => {
  const { defaultPageSize = 20, immediate = true } = options

  const paramsOverride = ref<TQuery>() as Ref<TQuery | undefined>
  const list = ref<R[]>([]) as Ref<R[]>
  const loading = ref(false)
  const pagination = ref<TablePagination>({
    currentPage: 1,
    pageSize: defaultPageSize,
    total: 0,
  })
  let skipPaginationRequest = false

  const requestList = async () => {
    const requestParams: PageRequest<TQuery> = {
      ...toValue(options.params ?? ({} as TQuery)),
      ...paramsOverride.value,
      currentPage: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
    }
    const formattedParams = options.formatParams
      ? options.formatParams(requestParams)
      : requestParams

    loading.value = true
    try {
      const { data } = await api(formattedParams)
      const result = options.formatResult
        ? options.formatResult(data)
        : { list: data.list, total: data.total }
      list.value = result.list
      pagination.value.total = result.total
      options.onSuccess?.(data)
    } catch {
      list.value = []
      pagination.value.total = 0
    } finally {
      loading.value = false
    }
  }

  const resetPage = async () => {
    if (pagination.value.currentPage === 1) {
      return
    }

    skipPaginationRequest = true
    pagination.value.currentPage = 1
    await nextTick()
  }

  const getList = async (newParams?: TQuery) => {
    if (newParams !== undefined) {
      paramsOverride.value = newParams
      await resetPage()
    }
    await requestList()
  }

  const search = async () => {
    await resetPage()
    await requestList()
  }

  watch(
    () => [pagination.value.currentPage, pagination.value.pageSize],
    () => {
      if (skipPaginationRequest) {
        skipPaginationRequest = false
        return
      }
      void requestList()
    },
  )

  if (immediate) {
    onMounted(requestList)
  }

  return {
    list,
    loading,
    pagination,
    getList,
    search,
  }
}
