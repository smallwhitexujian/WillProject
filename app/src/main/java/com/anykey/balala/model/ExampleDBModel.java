package com.anykey.balala.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by xujian on 15/8/27.
 * 示例
 */
@Table(name = DBConst.TABLE_FOR_MESSAGE)
public class ExampleDBModel extends Model implements Serializable {
    //@Column 设置字段名，如果不设置那么默认使用:name
    //index 设置索引
    @Column(name = "loginId")
    public String loginId;
    @Column(name = "Name")
    public String name;

    //需要默认的构造函数
    public ExampleDBModel() {
        super();
    }

    public ExampleDBModel(String id, String name) {
        super();
        this.name = name;
        this.loginId = id;
    }

    @Override
    public String toString() {
        return "ExampleDBModel{" +
                "name='" + name + '\'' +
                ", id='" + loginId + '\'' +
                '}';
    }

    /**
     基本使用方法:
     保存：
     ExampleDBModel dbModel1 = new ExampleDBModel("captain_miao_1", "hangzhou");
     dbModel1.save();
     查询:
     List<ExampleDBModel> dbModelList = new Select().from(ExampleDBModel.class).execute();
     ExampleDBModel model_query = new Select()
     .from(ExampleDBModel.class)
     .where("name = ", "captain_miao_1")
     .executeSingle();

     删除：
     model_query.delete();

     }
     查询：
     List<ExampleDBModel> exampleDBModel = new Select().from(ExampleDBModel.class).limit(10).orderBy("id ASC").execute();
     if (exampleDBModel.size() <= 0){
        return;
     }else{
        for (int i = 0;i<exampleDBModel.size(); i++) {
            DebugLogs.e("--------->"+exampleDBModel.get(i).name.toString());
        }
     }
     */
}
