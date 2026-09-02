<script setup lang="ts">
import { encryptionApi } from '@/api/encryption'
import type { EncryptionTestResponse } from '@/api/encryption/type'
import { getEncryptionRuntime } from '@/utils/apiEncryption'
import { LockKeyhole, Send, ShieldCheck } from '@lucide/vue'
import { onMounted, ref } from 'vue'

defineOptions({ name: 'EncryptionPage' })

const name = ref('TechLab')
const message = ref('这是一条双向加密的测试消息')
const loading = ref(false)
const result = ref<EncryptionTestResponse>()
const encryptionEnabled = ref<boolean>()

onMounted(async () => {
  encryptionEnabled.value = (await getEncryptionRuntime()).enabled
})

const submit = async () => {
  loading.value = true
  try {
    result.value = (await encryptionApi.test({ name: name.value, message: message.value })).data
    ElMessage.success('加密请求成功')
  } catch {
    result.value = undefined
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="min-h-[calc(100vh-64px)] bg-slate-950 px-6 py-16 text-slate-100">
    <section class="mx-auto max-w-3xl">
      <div class="mb-8 flex items-center gap-4">
        <div class="rounded-lg bg-emerald-400/10 p-3 text-emerald-300">
          <ShieldCheck :size="32" />
        </div>
        <div>
          <p class="mb-1 text-sm font-medium tracking-widest text-emerald-300">TECHLAB · 01</p>
          <h1 class="text-3xl font-semibold">接口双向加密实验</h1>
        </div>
      </div>

      <div class="rounded-lg border border-slate-800 bg-slate-900/70 p-8 shadow-2xl">
        <div class="mb-8 grid gap-3 text-sm text-slate-400 sm:grid-cols-3">
          <div class="rounded-lg bg-slate-950/70 px-4 py-3">AES-256-GCM</div>
          <div class="rounded-lg bg-slate-950/70 px-4 py-3">RSA-OAEP-256</div>
          <div class="rounded-lg bg-slate-950/70 px-4 py-3">
            {{
              encryptionEnabled === undefined
                ? '正在读取配置'
                : encryptionEnabled
                  ? '每请求随机密钥'
                  : '当前使用普通 JSON'
            }}
          </div>
        </div>

        <div class="space-y-5">
          <label class="block">
            <span class="mb-2 block text-sm text-slate-300">名称</span>
            <el-input v-model="name" size="large" placeholder="请输入名称" />
          </label>
          <label class="block">
            <span class="mb-2 block text-sm text-slate-300">测试消息</span>
            <el-input
              v-model="message"
              :rows="4"
              type="textarea"
              placeholder="请输入要加密发送的内容"
            />
          </label>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="encryptionEnabled === undefined || !name.trim() || !message.trim()"
            class="w-full!"
            @click="submit"
          >
            <Send v-if="!loading" :size="17" class="mr-2" />
            {{ encryptionEnabled === false ? '发送普通请求' : '发送加密请求' }}
          </el-button>
        </div>

        <div
          v-if="result"
          class="mt-8 rounded-lg border border-emerald-400/20 bg-emerald-400/5 p-5"
        >
          <div class="mb-4 flex items-center gap-2 font-medium text-emerald-300">
            <LockKeyhole :size="18" />
            {{ encryptionEnabled === false ? '普通响应' : '响应已解密' }}
          </div>
          <dl class="grid gap-3 text-sm sm:grid-cols-[120px_1fr]">
            <dt class="text-slate-500">服务端问候</dt>
            <dd>{{ result.greeting }}</dd>
            <dt class="text-slate-500">收到的消息</dt>
            <dd>{{ result.receivedMessage }}</dd>
            <dt class="text-slate-500">服务端时间</dt>
            <dd>{{ result.serverTime }}</dd>
          </dl>
        </div>
      </div>

      <p class="mt-5 text-center text-xs text-slate-600">
        {{
          encryptionEnabled === undefined
            ? '正在读取 application.yaml 接口加密配置'
            : encryptionEnabled
              ? '公钥接口保持明文，用于协商本次运行时生成的 RSA 公钥'
              : 'application.yaml 已关闭接口加密'
        }}
      </p>
    </section>
  </main>
</template>
