<script setup lang="ts">
import {BookOpen, FlaskConical, ShieldCheck} from '@lucide/vue'
import {ElConfigProvider} from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <div class="min-h-screen bg-zinc-50 text-zinc-900">
    <header class="h-16 border-b border-zinc-200 bg-white">
      <div class="mx-auto flex h-full max-w-7xl items-center justify-between px-4 sm:px-6">
        <RouterLink to="/dictionary" class="flex items-center gap-2 font-semibold">
          <FlaskConical :size="20" class="text-emerald-600"/>
          <span>TechLab</span>
        </RouterLink>
        <nav class="flex items-center gap-1" aria-label="实验导航">
          <RouterLink
            to="/dictionary"
            class="flex h-9 items-center gap-2 rounded-md px-3 text-sm text-zinc-600 transition-colors hover:bg-zinc-100 hover:text-zinc-950"
            active-class="bg-zinc-900! text-white! hover:bg-zinc-900! hover:text-white!"
          >
            <BookOpen :size="16"/>
            <span class="hidden sm:inline">数据字典</span>
          </RouterLink>
          <RouterLink
            to="/encryption"
            class="flex h-9 items-center gap-2 rounded-md px-3 text-sm text-zinc-600 transition-colors hover:bg-zinc-100 hover:text-zinc-950"
            active-class="bg-zinc-900! text-white! hover:bg-zinc-900! hover:text-white!"
          >
            <ShieldCheck :size="16"/>
            <span class="hidden sm:inline">接口加密</span>
          </RouterLink>
        </nav>
      </div>
    </header>
    <el-config-provider :locale="zhCn">
      <RouterView/>
    </el-config-provider>
  </div>
</template>
