package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: TagSet</p>
 * <p>Description: 标签类实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagSet extends GenericResult {
    /**
     * 标签属性
     */
    private Map<String, String> tags = new HashMap<>();

    public void setTag(String key, String value) {
        tags.put(key, value);
    }

}
