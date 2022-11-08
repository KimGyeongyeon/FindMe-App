package com.example.findme.domain.model;

public class MissingContainer {
    String image_url;
    String name;
    String docId;

    public MissingContainer(String image_url, String name, String docId) {
        this.image_url = image_url;
        this.name = name;
        this.docId = docId;
    }

    public String getName() {
        return this.name;
    }

    public String getImage_url() {
        return this.image_url;
    }

    public String getDocId() {
        return this.getDocId();
    }
}
