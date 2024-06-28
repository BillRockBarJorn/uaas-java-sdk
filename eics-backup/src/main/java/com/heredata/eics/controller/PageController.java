package com.heredata.eics.controller;

import com.sitech.cmap.fw.core.wsg.WsgPageResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @GetMapping("/data")
    @ResponseBody
    public WsgPageResult data() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 填充数据...
        Map<String, Object> map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        map = new HashMap<>();
        map.put("bucket", "bucket1");
        map.put("fileName", "xxxx1");
        map.put("size", "1234");
        map.put("versionId", "aaaaa");
        map.put("createTime", "2024-06-05");
        list.add(map);
        return new WsgPageResult(list, 1, 3, 6);
    }


    @GetMapping("")
    public String table() {
        return "index.html"; // 对应src/main/resources/templates/layui/table.html
    }

}
