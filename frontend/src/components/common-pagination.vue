<script setup lang="ts">
import type { TablePagination } from '@/hooks/useTable'

defineOptions({ name: 'CommonPagination' })

const pagination = defineModel<TablePagination>('pagination', { required: true })
</script>

<template>
  <el-pagination
    v-model:current-page="pagination.currentPage"
    v-model:page-size="pagination.pageSize"
    :page-sizes="[10, 20, 50, 100]"
    :total="pagination.total"
    background
    layout="total, prev, pager, next, sizes"
  />
</template>
