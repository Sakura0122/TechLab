package com.sakura.techlab.modules.dictionary.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.sakura.techlab.common.PageVo;
import com.sakura.techlab.common.ResultCodeEnum;
import com.sakura.techlab.exception.BusinessException;
import com.sakura.techlab.modules.dictionary.constant.DictionaryTypeConstant;
import com.sakura.techlab.modules.dictionary.dto.DictionaryItemSaveRequest;
import com.sakura.techlab.modules.dictionary.dto.DictionaryPageRequest;
import com.sakura.techlab.modules.dictionary.dto.DictionarySaveRequest;
import com.sakura.techlab.modules.dictionary.entity.Dictionary;
import com.sakura.techlab.modules.dictionary.entity.DictionaryItem;
import com.sakura.techlab.modules.dictionary.mapper.DictionaryMapper;
import com.sakura.techlab.modules.dictionary.service.DictionaryItemService;
import com.sakura.techlab.modules.dictionary.service.DictionaryService;
import com.sakura.techlab.modules.dictionary.vo.DictionaryDetailVo;
import com.sakura.techlab.modules.dictionary.vo.DictionaryItemVo;
import com.sakura.techlab.modules.dictionary.vo.DictionaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
* @author sakura
* @description 针对表【data_dictionary(数据字典表)】的数据库操作Service实现
* @createDate 2026-09-02 15:43:25
*/
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
    implements DictionaryService {

    private final DictionaryItemService dictionaryItemService;

    @Override
    public PageVo<DictionaryVo> pageDictionaries(DictionaryPageRequest request) {
        // 1. 校验字典类型筛选条件
        if (request.getType() != null) {
            validateDictionaryType(request.getType());
        }

        // 2. 构建分页和查询条件
        Page<Dictionary> page = request.toMpPageDefaultUpdateTimeDesc();
        var wrapper = request.toMpQueryWrapper(Dictionary::getName, Dictionary::getCode);
        wrapper.eq(request.getType() != null, Dictionary::getType, request.getType());

        // 3. 查询并转换分页结果
        return PageVo.of(page(page, wrapper), DictionaryVo.class);
    }

    @Override
    public DictionaryDetailVo getDictionary(Long id) {
        // 1. 查询字典主记录
        Dictionary dictionary = getById(id);
        if (dictionary == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "字典不存在");
        }

        // 2. 按排序值查询字典项
        List<DictionaryItem> items = dictionaryItemService.lambdaQuery()
                .eq(DictionaryItem::getDictionaryId, id)
                .orderByAsc(DictionaryItem::getSort, DictionaryItem::getId)
                .list();

        // 3. 组装详情结果
        DictionaryDetailVo detail = BeanUtil.copyProperties(dictionary, DictionaryDetailVo.class);
        detail.setItems(BeanUtil.copyToList(items, DictionaryItemVo.class));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictionaryDetailVo createDictionary(DictionarySaveRequest request) {
        // 1. 校验字典类型及编号
        validateDictionaryType(request.getType());
        validateDictionaryCode(request.getCode(), null);
        validateItemCodes(request.getItems());

        // 2. 保存字典主记录
        Dictionary dictionary = BeanUtil.copyProperties(request, Dictionary.class);
        save(dictionary);

        // 3. 保存字典项
        replaceItems(dictionary.getId(), request.getItems());

        // 4. 返回完整字典详情
        return getDictionary(dictionary.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictionaryDetailVo updateDictionary(Long id, DictionarySaveRequest request) {
        // 1. 校验字典是否存在、类型及编号是否可用
        if (getById(id) == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "字典不存在");
        }
        validateDictionaryType(request.getType());
        validateDictionaryCode(request.getCode(), id);
        validateItemCodes(request.getItems());

        // 2. 更新字典主记录
        Dictionary dictionary = BeanUtil.copyProperties(request, Dictionary.class);
        dictionary.setId(id);
        updateById(dictionary);

        // 3. 重建字典项
        replaceItems(id, request.getItems());

        // 4. 返回完整字典详情
        return getDictionary(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictionary(Long id) {
        // 1. 校验字典是否存在
        if (getById(id) == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND_ERROR, "字典不存在");
        }

        // 2. 删除所属字典项
        dictionaryItemService.lambdaUpdate()
                .eq(DictionaryItem::getDictionaryId, id)
                .remove();

        // 3. 删除字典主记录
        removeById(id);
    }

    /**
     * 物理删除指定字典的原有字典项，并按本次请求重新批量保存。
     *
     * @param dictionaryId 字典ID
     * @param requests     本次提交的字典项
     */
    private void replaceItems(Long dictionaryId, List<DictionaryItemSaveRequest> requests) {
        // 1. 物理删除原有字典项
        dictionaryItemService.lambdaUpdate()
                .eq(DictionaryItem::getDictionaryId, dictionaryId)
                .remove();

        // 2. 转换本次提交的字典项
        List<DictionaryItem> items = requests.stream()
                .map(request -> {
                    DictionaryItem item = BeanUtil.copyProperties(request, DictionaryItem.class);
                    item.setDictionaryId(dictionaryId);
                    return item;
                })
                .toList();

        // 3. 批量保存新字典项
        if (!items.isEmpty()) {
            dictionaryItemService.saveBatch(items);
        }
    }

    /**
     * 校验字典类型是否为系统字典或业务字典。
     *
     * @param type 待校验的字典类型
     * @throws BusinessException 字典类型不受支持时抛出
     */
    private void validateDictionaryType(Integer type) {
        if (!DictionaryTypeConstant.isValid(type)) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "字典类型不正确");
        }
    }

    /**
     * 校验字典编号是否已被其他字典使用。
     *
     * @param code       待校验的字典编号
     * @param excludedId 编辑时需要排除的字典ID，新增时为 {@code null}
     * @throws BusinessException 字典编号已存在时抛出
     */
    private void validateDictionaryCode(String code, Long excludedId) {
        var query = lambdaQuery().eq(Dictionary::getCode, code.trim());
        if (excludedId != null) {
            query.ne(Dictionary::getId, excludedId);
        }
        if (query.exists()) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "字典编号已存在");
        }
    }

    /**
     * 校验同一次提交中的字典项编号是否重复，编号比较忽略大小写和首尾空格。
     *
     * @param items 待校验的字典项
     * @throws BusinessException 存在重复编号时抛出
     */
    private void validateItemCodes(List<DictionaryItemSaveRequest> items) {
        Set<String> codes = new HashSet<>();
        for (DictionaryItemSaveRequest item : items) {
            if (!codes.add(item.getCode().trim().toLowerCase(Locale.ROOT))) {
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR, "同一字典下的字典项编号不能重复");
            }
        }
    }
}
