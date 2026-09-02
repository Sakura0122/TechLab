package com.sakura.techlab.modules.dictionary.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.sakura.techlab.modules.dictionary.entity.DictionaryItem;
import com.sakura.techlab.modules.dictionary.mapper.DictionaryItemMapper;
import com.sakura.techlab.modules.dictionary.service.DictionaryItemService;
import org.springframework.stereotype.Service;

/**
* @author sakura
* @description 针对表【data_dictionary_item(数据字典项表)】的数据库操作Service实现
* @createDate 2026-09-02 15:43:25
*/
@Service
public class DictionaryItemServiceImpl extends ServiceImpl<DictionaryItemMapper, DictionaryItem>
    implements DictionaryItemService {

}



