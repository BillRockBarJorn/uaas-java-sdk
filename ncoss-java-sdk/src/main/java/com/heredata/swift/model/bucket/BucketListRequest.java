package com.heredata.swift.model.bucket;

import com.heredata.exception.ClientException;
import com.heredata.model.WebServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketListRequest extends WebServiceRequest {

    public static final int MAX_RETURNED_KEYS = 1000;

    /**
     * 限制查询出的数量
     */
    private Integer limit;

    /**
     * 限制查询出的桶名称大于该值
     */
    private String startAfter;

    /**
     * 限制查询出桶的名称的前缀
     */
    private String prefix;

    public void setLimit(Integer limit) {
        if (limit <= 0 || limit > MAX_RETURNED_KEYS) {
            throw new ClientException("limit requires (0,1000]");
        }
        this.limit = limit;
    }
}
