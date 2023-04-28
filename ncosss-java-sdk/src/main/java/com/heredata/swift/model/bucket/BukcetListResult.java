package com.heredata.swift.model.bucket;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BukcetListResult extends GenericResult {

    // 限制查询桶的数量
    private Integer limit;

    // 限制桶名称的大于某个字典序
    private String startAfter;

    // 限制查询桶名称的前缀
    private String prefix;

    // 账户下桶列表
    private List<Bucket> bucketList;

    private Boolean truncated;

}
