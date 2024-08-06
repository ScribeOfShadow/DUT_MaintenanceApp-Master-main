package com.example.dutmaintenance;

public class Data {

    private String title;
    private String location;
    private String description;

    private String status;

    private String image;

    private String id;

    public Data() {
    }

    public Data(String title, String location, String description, String status, String image, String id) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.status = status;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
