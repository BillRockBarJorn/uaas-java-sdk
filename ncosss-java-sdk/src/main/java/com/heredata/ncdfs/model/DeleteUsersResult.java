package com.heredata.ncdfs.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUsersResult extends GenericResult {

    private List<String> success = new ArrayList<>();

    private List<JSONObject> failed = new ArrayList<>();
}
