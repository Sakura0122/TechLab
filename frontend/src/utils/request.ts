import type { Data } from '@/types/common'
import type { AxiosRequestConfig, Method } from 'axios'
import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
})

service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

service.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) {
      return res.data
    } else {
      ElMessage.error(res.data.message || '网络异常')
      return Promise.reject(res.data)
    }
  },
  (error) => {
    ElMessage.error('请求错误')
    return Promise.reject(error)
  },
)

const baseRequest = (method: Method) => {
  return <T>(url: string, submitData?: object, config?: AxiosRequestConfig) => {
    return service.request<T, Data<T>>({
      url,
      method,
      [method.toLowerCase() === 'get' ? 'params' : 'data']: submitData,
      ...config,
    })
  }
}

const request = {
  get: baseRequest('get'),
  post: baseRequest('post'),
  put: baseRequest('put'),
  delete: baseRequest('delete'),
}

export default request
