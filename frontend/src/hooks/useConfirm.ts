import { ElMessage, ElMessageBox, type ElMessageBoxOptions } from "element-plus";

export type ConfirmOptions<T = any> = {
  title?: string;
  message?: string | ((params: T) => string);
  successMsg?: string | ((params: T) => string);
  boxOptions?: Partial<Omit<ElMessageBoxOptions, "title" | "message">>;
};

export const CONFIRM_PRESETS = {
  delete: {
    title: "提示",
    message: "是否确认删除该数据？",
    successMsg: "删除成功",
    boxOptions: { type: "warning" }
  },
  submit: {
    title: "提示",
    message: "是否确认提交？",
    successMsg: "提交成功",
    boxOptions: { type: "info" }
  },
  enable: {
    title: "提示",
    message: "是否确认启用？",
    successMsg: "启用成功",
    boxOptions: { type: "warning" }
  },
  disable: {
    title: "提示",
    message: "是否确认停用？",
    successMsg: "停用成功",
    boxOptions: { type: "warning" }
  }
} as const;

/**
 * 通用二次确认 Hook
 * @param apiFn 执行的 API 函数
 * @param refreshCallback 成功后的回调
 * @param options 自定义配置
 */
export const useConfirm = <T = any>(
  apiFn: (params: T) => Promise<any>,
  refreshCallback: () => void,
  options: ConfirmOptions<T> = {}
) => {
  const { title = "提示", message = "是否确认执行该操作？", successMsg = "操作成功", boxOptions = {} } = options;

  const handleConfirm = async (params: T) => {
    try {
      const finalMessage = typeof message === "function" ? message(params) : message;

      await ElMessageBox.confirm(finalMessage, title, {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        ...boxOptions
      });

      await apiFn(params);

      const finalSuccessMsg = typeof successMsg === "function" ? successMsg(params) : successMsg;
      ElMessage.success(finalSuccessMsg);

      refreshCallback();
    } catch (error) {
      console.log("操作取消或失败", error);
    }
  };

  return { handleConfirm };
};

const createConfirmHook = <M extends string>(presetKey: keyof typeof CONFIRM_PRESETS, methodName: M) => {
  return <T = any>(
    apiFn: (params: T) => Promise<any>,
    refreshCallback: () => void,
    options: ConfirmOptions<T> = {}
  ): Record<M, (params: T) => Promise<void>> => {
    const { handleConfirm } = useConfirm(apiFn, refreshCallback, {
      ...CONFIRM_PRESETS[presetKey],
      ...options
    });
    return { [methodName]: handleConfirm } as Record<M, (params: T) => Promise<void>>;
  };
};

export const useDelete = createConfirmHook("delete", "handleDelete");
export const useSubmit = createConfirmHook("submit", "handleSubmit");
export const useEnable = createConfirmHook("enable", "handleEnable");
export const useDisable = createConfirmHook("disable", "handleDisable");
