package com.sakura.techlab.common;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageVo<T> {
    @Schema(description = "总条数")
    private Integer total;

    @Schema(description = "总页数")
    private Integer pageCount;

    @Schema(description = "当前页数据")
    private List<T> list;

    /**
     * 将MybatisPlus分页结果转为 VO分页结果
     *
     * @param p       MybatisPlus的分页结果
     * @param voClass 目标VO类型的字节码
     * @param <V>     目标VO类型
     * @param <P>     原始PO类型
     * @return VO的分页对象
     */
    public static <V, P> PageVo<V> of(Page<P> p, Class<V> voClass) {
        // 1.非空校验
        List<P> records = p.getRecords();

        // 2.数据转换
        List<V> vos = BeanUtil.copyToList(records, voClass);

        // 3.封装返回
        return new PageVo<>((int) p.getTotal(), (int) p.getPages(), vos);
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果，允许用户自定义PO到VO的转换方式
     *
     * @param p         MybatisPlus的分页结果
     * @param convertor PO到VO的转换函数
     * @param <V>       目标VO类型
     * @param <P>       原始PO类型
     * @return VO的分页对象
     */
    public static <V, P> PageVo<V> of(Page<P> p, Function<P, V> convertor) {
        // 1.非空校验
        List<P> records = p.getRecords();

        // 2.数据转换
        List<V> vos = records.stream().map(convertor).collect(Collectors.toList());

        // 3.封装返回
        return new PageVo<>((int) p.getTotal(), (int) p.getPages(), vos);
    }
}