import type { EncryptionTestRequest, EncryptionTestResponse } from './type'
import request from '@/utils/request'

export const encryptionApi = {
  test(data: EncryptionTestRequest) {
    return request.post<EncryptionTestResponse>('/test/encryption', data, {
      apiEncrypted: true,
    })
  },
}
