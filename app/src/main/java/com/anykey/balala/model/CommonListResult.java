package com.anykey.balala.model;

import java.util.List;

/**
 * Created by xujian on 15/9/8.
 */
public class CommonListResult<T> extends CommonModel {
    public List<T> data;
    public boolean hasData() {
        return data != null && data.size() > 0;
    }
}
