import type { Data } from '@/types/common'
import axios from 'axios'

interface EncryptionConfig {
  enabled: boolean
  keyId: string
  algorithm: string
  publicKey: string
}

export interface EncryptedRequest {
  keyId: string
  encryptedKey: string
  iv: string
  ciphertext: string
  timestamp: number
  requestId: string
}

export interface EncryptedResponse {
  iv: string
  ciphertext: string
}

export interface RequestEncryptionContext {
  key: CryptoKey
  requestId: string
}

type EncryptionRuntime =
  | { enabled: false; keyId: string }
  | { enabled: true; keyId: string; publicKey: CryptoKey }

const encoder = new TextEncoder()
const decoder = new TextDecoder()
let runtimePromise: Promise<EncryptionRuntime> | undefined

const toBase64Url = (value: ArrayBuffer) => {
  let binary = ''
  for (const byte of new Uint8Array(value)) {
    binary += String.fromCharCode(byte)
  }
  return btoa(binary).replaceAll('+', '-').replaceAll('/', '_').replace(/=+$/, '')
}

const fromBase64Url = (value: string) => {
  const base64 = value
    .replaceAll('-', '+')
    .replaceAll('_', '/')
    .padEnd(value.length + ((4 - (value.length % 4)) % 4), '=')
  return Uint8Array.from(atob(base64), (character) => character.charCodeAt(0))
}

const loadRuntime = async (): Promise<EncryptionRuntime> => {
  const { data: result } = await axios.get<Data<EncryptionConfig>>('/api/api-encryption/public-key')
  if (result.code !== 200) {
    throw new Error(result.message)
  }
  if (!result.data.enabled) {
    return { enabled: false, keyId: result.data.keyId }
  }
  if (result.data.algorithm !== 'AES-256-GCM+RSA-OAEP-256') {
    throw new Error(`不支持的接口加密算法：${result.data.algorithm}`)
  }
  return {
    enabled: true,
    keyId: result.data.keyId,
    publicKey: await crypto.subtle.importKey(
      'spki',
      fromBase64Url(result.data.publicKey),
      { name: 'RSA-OAEP', hash: 'SHA-256' },
      false,
      ['encrypt'],
    ),
  }
}

export const getEncryptionRuntime = () => (runtimePromise ??= loadRuntime())

export const encryptRequest = async (
  data: unknown,
  method: string,
  path: string,
  runtime: Extract<EncryptionRuntime, { enabled: true }>,
) => {
  const key = await crypto.subtle.generateKey({ name: 'AES-GCM', length: 256 }, true, [
    'encrypt',
    'decrypt',
  ])
  const rawKey = await crypto.subtle.exportKey('raw', key)
  const iv = crypto.getRandomValues(new Uint8Array(12))
  const timestamp = Date.now()
  const requestId = crypto.randomUUID()
  const aad = encoder.encode([method, path, runtime.keyId, timestamp, requestId].join('\n'))
  const ciphertext = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv, additionalData: aad, tagLength: 128 },
    key,
    encoder.encode(JSON.stringify(data)),
  )
  const encryptedKey = await crypto.subtle.encrypt({ name: 'RSA-OAEP' }, runtime.publicKey, rawKey)

  return {
    payload: {
      keyId: runtime.keyId,
      encryptedKey: toBase64Url(encryptedKey),
      iv: toBase64Url(iv.buffer),
      ciphertext: toBase64Url(ciphertext),
      timestamp,
      requestId,
    } satisfies EncryptedRequest,
    context: { key, requestId } satisfies RequestEncryptionContext,
  }
}

export const decryptResponse = async (
  payload: EncryptedResponse,
  context: RequestEncryptionContext,
) => {
  const plaintext = await crypto.subtle.decrypt(
    {
      name: 'AES-GCM',
      iv: fromBase64Url(payload.iv),
      additionalData: encoder.encode(`response\n${context.requestId}`),
      tagLength: 128,
    },
    context.key,
    fromBase64Url(payload.ciphertext),
  )
  return JSON.parse(decoder.decode(plaintext)) as unknown
}
