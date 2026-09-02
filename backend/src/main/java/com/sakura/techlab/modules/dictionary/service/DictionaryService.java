package com.sakura.techlab.modules.dictionary.service;

import com.sakura.techlab.modules.dictionary.entity.Dictionary;
import com.baomidou.mybatisplus.spring.service.IService;
import com.sakura.techlab.common.PageVo;
import com.sakura.techlab.modules.dictionary.dto.DictionaryPageRequest;
import com.sakura.techlab.modules.dictionary.dto.DictionarySaveRequest;
import com.sakura.techlab.modules.dictionary.vo.DictionaryDetailVo;
import com.sakura.techlab.modules.dictionary.vo.DictionaryVo;

/**
* @author sakura
* @description 针对表【data_dictionary(数据字典表)】的数据库操作Service
* @createDate 2026-09-02 15:43:25
*/
public interface DictionaryService extends IService<Dictionary> {

    PageVo<DictionaryVo> pageDictionaries(DictionaryPageRequest request);

    DictionaryDetailVo getDictionary(Long id);

    DictionaryDetailVo createDictionary(DictionarySaveRequest request);

    DictionaryDetailVo updateDictionary(Long id, DictionarySaveRequest request);

    void deleteDictionary(Long id);
}
