package com.zjz.pojo;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "分页结果")
public class ToPage<T> {
    @ApiModelProperty("总条数")
    private Long total;
    @ApiModelProperty("总页数")
    private Long pages;
    @ApiModelProperty("集合")
    private List<T> list;

    /**
     * 返回空分页结果
     * @param p MybatisPlus的分页结果
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <P> ToPage<P> empty(Page<P> p){
        return new ToPage<>(p.getTotal(), p.getPages(), Collections.emptyList());
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果
     * @param p MybatisPlus的分页结果
     * @param voClass 目标VO类型的字节码
     * @param <P> 原始PO类型
     * @return VO的分页对象
     */
    public static <P> ToPage<P> of(Page<P> p, Class<P> voClass) {
        // 1.非空校验
        List<P> records = p.getRecords();
        if (records == null || records.size() <= 0) {
            // 无数据，返回空结果
            return empty(p);
        }
        // 2.数据转换
        List<P> list = BeanUtil.copyToList(records, voClass);
        // 3.封装返回
        return new ToPage<>(p.getTotal(), p.getPages(), list);
    }
}
