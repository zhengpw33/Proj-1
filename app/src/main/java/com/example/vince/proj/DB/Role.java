package com.example.vince.proj.DB;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by vince on 2017/11/13.
 */

public class Role extends DataSupport {

    @Column(unique = true)
    private int Id;

    //图片资源
    private int ImageId;

    //姓名
    @Column(unique = true)
    private String Name;

    //字
    private String SubName;

    //性别
    private String Gender;

    //生卒年
    private String LifeTime;

    //籍贯
    private String NativePlace;

    //介绍
    private String Description;

    //所属势力
    private String Nationality;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String subName) {
        SubName = subName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLifeTime() {
        return LifeTime;
    }

    public void setLifeTime(String lifeTime) {
        LifeTime = lifeTime;
    }

    public String getNativePlace() {
        return NativePlace;
    }

    public void setNativePlace(String nativePlace) {
        NativePlace = nativePlace;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }
}
