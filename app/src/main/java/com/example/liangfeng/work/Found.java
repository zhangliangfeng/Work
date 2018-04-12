package com.example.liangfeng.work;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by liangfeng on 2018/1/5.
 */

public class Found extends BmobObject {
    private String title;
    private String describe;
    private String phone;
    private BmobFile picture;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }
}

