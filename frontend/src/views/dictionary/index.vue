<script setup lang="ts">
import { dictionaryApi } from '@/api/dictionary'
import type { Dictionary, DictionaryQuery } from '@/api/dictionary/type'
import {
  DICTIONARY_TYPE,
  DICTIONARY_TYPE_FILTER_ALL,
  DICTIONARY_TYPE_LABELS,
  type DictionaryType,
} from '@/constants/dictionary'
import DictionaryDrawer from '@/views/dictionary/components/dictionary-drawer.vue'
import CommonPagination from '@/components/common-pagination.vue'
import { useDelete } from '@/hooks/useConfirm'
import { useTable } from '@/hooks/useTable'
import type { PageRequest } from '@/types/common'
import { BookOpen, Pencil, Plus, Search, Trash2 } from '@lucide/vue'
import { ref, useTemplateRef } from 'vue'

defineOptions({ name: 'DictionaryPage' })

interface DictionaryDrawerExpose {
  open: (id?: string) => Promise<void>
}

const drawerRef = useTemplateRef<DictionaryDrawerExpose>('drawerRef')
const queryParams = ref<DictionaryQuery>({
  keyword: '',
  type: DICTIONARY_TYPE_FILTER_ALL,
})
const { list, loading, pagination, search } = useTable(
  (params: PageRequest<DictionaryQuery>) => dictionaryApi.page(params),
  { params: queryParams },
)

const getDictionaryTypeLabel = (type: DictionaryType) => DICTIONARY_TYPE_LABELS[type]

const { handleDelete } = useDelete<Dictionary>(
  (dictionary) => dictionaryApi.delete(dictionary.id),
  () => void search(),
)
</script>

<template>
  <main class="mx-auto max-w-7xl px-4 py-8 sm:px-6 sm:py-10">
    <div class="mb-8 flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
      <div>
        <p class="mb-2 text-sm font-medium text-emerald-700">TECHLAB · 02</p>
        <h1 class="text-2xl font-semibold text-zinc-950">数据字典</h1>
      </div>
      <el-button type="primary" size="large" @click="drawerRef?.open()">
        <Plus :size="17" />
        新增字典
      </el-button>
    </div>

    <section class="border-y border-zinc-200 bg-white">
      <div class="flex flex-col gap-3 border-b border-zinc-200 px-4 py-4 lg:flex-row">
        <el-input
          v-model="queryParams.keyword"
          clearable
          class="sm:max-w-sm"
          placeholder="搜索名称或编号"
          @keyup.enter="search"
          @clear="search"
        >
          <template #prefix>
            <Search :size="16" />
          </template>
        </el-input>
        <el-button @click="search">查询</el-button>
        <el-radio-group v-model="queryParams.type" class="lg:ml-auto" @change="search">
          <el-radio-button :value="DICTIONARY_TYPE_FILTER_ALL">全部</el-radio-button>
          <el-radio-button :value="DICTIONARY_TYPE.SYSTEM">系统字典</el-radio-button>
          <el-radio-button :value="DICTIONARY_TYPE.BUSINESS">业务字典</el-radio-button>
        </el-radio-group>
      </div>

      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="name" label="字典名称" min-width="160" />
        <el-table-column prop="code" label="字典编号" min-width="180">
          <template #default="{ row }">
            <code class="text-xs text-zinc-700">{{ row.code }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" min-width="130">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ getDictionaryTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="text-zinc-600">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="170" />
        <el-table-column label="操作" width="150" align="right" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="drawerRef?.open(row.id)">
              <Pencil :size="15" />
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
            >
              <Trash2 :size="15" />
              删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="flex min-h-52 flex-col items-center justify-center text-zinc-400">
            <BookOpen :size="30" class="mb-3" />
            <span class="text-sm">暂无字典</span>
          </div>
        </template>
      </el-table>

      <div class="flex justify-end px-4 py-4">
        <CommonPagination :pagination />
      </div>
    </section>

    <DictionaryDrawer ref="drawerRef" @saved="search" />
  </main>
</template>
