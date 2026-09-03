export interface Data<T> {
  code: number
  message: string
  data: T
}

// 分页请求参数类型
export type PageRequest<T = any> = {
  currentPage: number
  pageSize: number
} & T

export type PageResult<T> = {
  total: number
  pageCount: number
  list: T[]
}
