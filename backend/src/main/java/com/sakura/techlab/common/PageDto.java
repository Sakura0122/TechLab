package com.sakura.techlab.common;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageDto {
    @Schema(description = "当前页数")
    @Min(value = 1, message = "当前页数不能小于1")
    private int currentPage = 1;

    @Schema(description = "每页显示条目个数")
    @Min(value = 1, message = "每页显示条目个数不能小于1")
    private int pageSize = 20;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "是否升序")
    private Boolean isAsc = true;

    @Schema(description = "搜索关键词")
    private String keyword;

    @SafeVarargs
    public final <T> LambdaQueryWrapper<T> toMpQueryWrapper(SFunction<T, ?>... searchFields) {
        LambdaQueryWrapper<T> wrapper = Wrappers.lambdaQuery();
        appendKeyword(wrapper, searchFields);
        return wrapper;
    }

    @SafeVarargs
    public final <T> LambdaQueryChainWrapper<T> applyKeyword(
            LambdaQueryChainWrapper<T> wrapper,
            SFunction<T, ?>... searchFields
    ) {
        appendKeyword(wrapper.getWrapper(), searchFields);
        return wrapper;
    }

    @SafeVarargs
    private final <T> void appendKeyword(
            AbstractWrapper<T, SFunction<T, ?>, LambdaQueryWrapper<T>> wrapper,
            SFunction<T, ?>... searchFields
    ) {
        if (keyword == null || keyword.isBlank() || searchFields == null || searchFields.length == 0) {
            return;
        }

        String value = keyword.trim();
        wrapper.and(query -> {
            for (int i = 0; i < searchFields.length; i++) {
                if (i > 0) {
                    query.or();
                }
                query.like(searchFields[i], value);
            }
        });
    }

    public <T> Page<T> toMpPage(OrderItem... orders) {
        // 1.分页条件
        Page<T> p = Page.of(currentPage, pageSize);
        // 2.排序条件
        // 2.1.先看前端有没有传排序字段
        if (sortField != null) {
            p.addOrder(new OrderItem().setColumn(sortField).setAsc(isAsc));
            return p;
        }
        // 2.2.再看有没有手动指定排序字段
        if (orders != null) {
            p.addOrder(orders);
        }
        return p;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        return this.toMpPage(new OrderItem().setColumn(defaultSortBy).setAsc(isAsc));
    }

    public <T> Page<T> toMpPageDefaultCreateTimeDesc() {
        return toMpPage(
                new OrderItem().setColumn("created_at").setAsc(false)
        );
    }

    public <T> Page<T> toMpPageDefaultUpdateTimeDesc() {
        return toMpPage(
                new OrderItem().setColumn("updated_at").setAsc(false)
        );
    }
}
