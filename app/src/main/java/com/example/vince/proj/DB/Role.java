package com.example.vince.proj.DB;

import org.litepal.crud.DataSupport;

/**
 * Created by vince on 2017/11/13.
 */

public class Role extends DataSupport {
    private int Id;
    private int ImageId;
    private String Name;
    private String Description;

    public Role(int id, int imageId, String name, String description) {
        Id = id;
        ImageId = imageId;
        Name = name;
        Description = description;
    }

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
}
