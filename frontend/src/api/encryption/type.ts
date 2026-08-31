export interface EncryptionTestRequest {
  name: string
  message: string
}

export interface EncryptionTestResponse {
  greeting: string
  receivedMessage: string
  serverTime: string
}
