<script setup lang="ts">
import { dictionaryApi } from '@/api/dictionary'
import type { DictionaryInput, DictionaryItemInput } from '@/api/dictionary/type'
import { DICTIONARY_TYPE_OPTIONS, type DictionaryType } from '@/constants/dictionary'
import { Plus, Save, Trash2 } from '@lucide/vue'
import type { FormInstance, FormRules } from 'element-plus'
import { nextTick, ref, useTemplateRef } from 'vue'

defineOptions({ name: 'DictionaryDrawer' })

interface EditableDictionaryItem extends DictionaryItemInput {
  clientKey: string
}

interface DictionaryForm extends Omit<DictionaryInput, 'items' | 'type'> {
  type?: DictionaryType
  items: EditableDictionaryItem[]
}

const emit = defineEmits<{
  saved: []
}>()

const visible = ref(false)
const editingId = ref<string>()
const detailLoading = ref(false)
const submitting = ref(false)
const formRef = useTemplateRef<FormInstance>('formRef')
let itemSequence = 0

const createItem = (item?: Partial<DictionaryItemInput>): EditableDictionaryItem => ({
  clientKey: `item-${itemSequence++}`,
  name: item?.name ?? '',
  code: item?.code ?? '',
  description: item?.description ?? '',
  status: item?.status ?? 1,
  sort: item?.sort ?? 0,
})

const createForm = (): DictionaryForm => ({
  name: '',
  code: '',
  type: undefined,
  description: '',
  items: [createItem()],
})

const form = ref<DictionaryForm>(createForm())
const rules: FormRules<DictionaryForm> = {
  name: [
    { required: true, message: '请输入字典名称', trigger: 'blur' },
    { max: 100, message: '字典名称不能超过100个字符', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入字典编号', trigger: 'blur' },
    { max: 100, message: '字典编号不能超过100个字符', trigger: 'blur' },
  ],
  type: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  description: [{ max: 500, message: '字典描述不能超过500个字符', trigger: 'blur' }],
}
const itemNameRules = [
  { required: true, message: '请输入名称', trigger: 'blur' },
  { max: 100, message: '不能超过100个字符', trigger: 'blur' },
]
const itemCodeRules = [
  { required: true, message: '请输入编号', trigger: 'blur' },
  { max: 100, message: '不能超过100个字符', trigger: 'blur' },
]

const open = async (id?: string) => {
  visible.value = true
  editingId.value = id
  form.value = createForm()
  await nextTick()
  formRef.value?.clearValidate()

  if (!id) {
    return
  }

  detailLoading.value = true
  try {
    const { data } = await dictionaryApi.detail(id)
    form.value = {
      name: data.name,
      code: data.code,
      type: data.type,
      description: data.description ?? '',
      items: data.items.map((item) =>
        createItem({
          name: item.name,
          code: item.code,
          description: item.description ?? '',
          status: item.status,
          sort: item.sort,
        }),
      ),
    }
  } catch {
    visible.value = false
  } finally {
    detailLoading.value = false
  }
}

const addItem = () => {
  form.value.items.push(createItem({ sort: form.value.items.length }))
}

const removeItem = (index: number) => {
  form.value.items.splice(index, 1)
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  const dictionaryType = form.value.type
  if (!valid || dictionaryType === undefined) {
    return
  }

  const itemCodes = form.value.items.map((item) => item.code.trim().toLocaleLowerCase())
  if (new Set(itemCodes).size !== itemCodes.length) {
    ElMessage.warning('同一字典下的字典项编号不能重复')
    return
  }

  const data: DictionaryInput = {
    name: form.value.name.trim(),
    code: form.value.code.trim(),
    type: dictionaryType,
    description: form.value.description.trim(),
    items: form.value.items.map((item) => ({
      name: item.name.trim(),
      code: item.code.trim(),
      description: item.description.trim(),
      status: item.status,
      sort: item.sort,
    })),
  }

  submitting.value = true
  try {
    if (editingId.value) {
      await dictionaryApi.update(editingId.value, data)
      ElMessage.success('字典更新成功')
    } else {
      await dictionaryApi.create(data)
      ElMessage.success('字典新增成功')
    }
    visible.value = false
    emit('saved')
  } catch {
    // 请求层统一展示错误信息。
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<template>
  <el-drawer
    v-model="visible"
    :title="editingId ? '编辑字典' : '新增字典'"
    size="min(920px, 100%)"
    :close-on-click-modal="!submitting"
    :close-on-press-escape="!submitting"
    :show-close="!submitting"
  >
    <div v-loading="detailLoading" class="min-h-full">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <section class="border-b border-zinc-200 pb-7">
          <h2 class="mb-5 text-base font-semibold text-zinc-900">字典信息</h2>
          <div class="grid gap-x-5 sm:grid-cols-2">
            <el-form-item label="字典名称" prop="name">
              <el-input v-model="form.name" maxlength="100" placeholder="请输入字典名称" />
            </el-form-item>
            <el-form-item label="字典编号" prop="code">
              <el-input v-model="form.code" maxlength="100" placeholder="请输入唯一编号" />
            </el-form-item>
            <el-form-item label="字典类型" prop="type">
              <el-select v-model="form.type" class="w-full" placeholder="请选择字典类型">
                <el-option
                  v-for="option in DICTIONARY_TYPE_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="字典描述" prop="description">
              <el-input
                v-model="form.description"
                maxlength="500"
                show-word-limit
                placeholder="请输入字典描述"
              />
            </el-form-item>
          </div>
        </section>

        <section class="pt-7">
          <div class="mb-5 flex items-center justify-between gap-4">
            <div class="flex items-baseline gap-2">
              <h2 class="text-base font-semibold text-zinc-900">字典项</h2>
              <span class="text-xs text-zinc-500">{{ form.items.length }} 项</span>
            </div>
            <el-button type="primary" plain @click="addItem">
              <Plus :size="16" />
              新增字典项
            </el-button>
          </div>

          <el-table :data="form.items" row-key="clientKey" empty-text="暂无字典项">
            <el-table-column label="名称" min-width="150">
              <template #default="{ row, $index }">
                <el-form-item :prop="`items.${$index}.name`" :rules="itemNameRules" class="mb-0!">
                  <el-input v-model="row.name" maxlength="100" placeholder="名称" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="编号" min-width="150">
              <template #default="{ row, $index }">
                <el-form-item :prop="`items.${$index}.code`" :rules="itemCodeRules" class="mb-0!">
                  <el-input v-model="row.code" maxlength="100" placeholder="编号" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="描述" min-width="180">
              <template #default="{ row }">
                <el-input v-model="row.description" maxlength="500" placeholder="描述" />
              </template>
            </el-table-column>
            <el-table-column label="排序" width="100" align="center">
              <template #default="{ row }">
                <el-input-number v-model="row.sort" :min="0" :controls="false" class="w-16!" />
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center" fixed="right">
              <template #default="{ $index }">
                <el-tooltip content="移除字典项" placement="top">
                  <el-button
                    text
                    type="danger"
                    circle
                    aria-label="移除字典项"
                    @click="removeItem($index)"
                  >
                    <Trash2 :size="16" />
                  </el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
        </section>
      </el-form>
    </div>

    <template #footer>
      <div class="flex justify-end gap-3">
        <el-button :disabled="submitting" @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">
          <Save v-if="!submitting" :size="16" />
          保存
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>
