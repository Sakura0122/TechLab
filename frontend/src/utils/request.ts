import type { Data } from '@/types/common'
import {
  decryptResponse,
  encryptRequest,
  getEncryptionRuntime,
  type EncryptedResponse,
  type RequestEncryptionContext,
} from '@/utils/apiEncryption'
import type { AxiosRequestConfig, Method } from 'axios'
import axios from 'axios'

declare module 'axios' {
  export interface AxiosRequestConfig {
    apiEncrypted?: boolean
  }

  export interface InternalAxiosRequestConfig {
    apiEncrypted?: boolean
    encryptionContext?: RequestEncryptionContext
  }
}

const service = axios.create({
  baseURL: '/api',
})

service.interceptors.request.use(
  async (config) => {
    if (!config.apiEncrypted || config.data === undefined) {
      return config
    }
    const runtime = await getEncryptionRuntime()
    if (!runtime.enabled) {
      return config
    }
    const { payload, context } = await encryptRequest(
      config.data,
      config.method!.toUpperCase(),
      new URL(config.url!, window.location.origin).pathname,
      runtime,
    )
    config.data = payload
    config.encryptionContext = context
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

service.interceptors.response.use(
  async (res) => {
    if (res.config.encryptionContext) {
      res.data = await decryptResponse(res.data as EncryptedResponse, res.config.encryptionContext)
    }
    if (res.data.code === 200) {
      return res.data
    }
    ElMessage.error(res.data.message || '网络异常')
    return Promise.reject(res.data)
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
