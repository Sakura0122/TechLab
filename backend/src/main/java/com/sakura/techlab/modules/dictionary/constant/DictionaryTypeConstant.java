package com.sakura.techlab.modules.dictionary.constant;

import java.util.Objects;

/**
 * 字典类型常量。
 */
public final class DictionaryTypeConstant {

    /** 系统字典。 */
    public static final int SYSTEM = 1;

    /** 业务字典。 */
    public static final int BUSINESS = 2;

    private DictionaryTypeConstant() {
    }

    public static boolean isValid(Integer type) {
        return Objects.equals(type, SYSTEM)
                || Objects.equals(type, BUSINESS);
    }
}
