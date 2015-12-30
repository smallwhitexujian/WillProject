package com.anykey.balala.model;

/**
 * Created by Shanli_pc on 2015/9/8.
 */
public class CommonData extends CommonModel {
    public String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonData)) return false;

        CommonData that = (CommonData) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CommonData{" +
                "data='" + data + '\'' +
                '}';
    }
}
