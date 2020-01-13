package com.example.red5proissue;


import org.w3c.dom.Element;

public class Red5PropertiesItem {
    private String id;
    private String content;
    private String className;
    private String description;

    private Element localProperties;

    public Red5PropertiesItem(String _id, String _content) {
        id = _id;
        content = _content;
    }

    public Red5PropertiesItem(String _id, Element contentXML) {
        id = _id;
        content = contentXML.getElementsByTagName("name").item(0).getTextContent();
        className = contentXML.getElementsByTagName("class").item(0).getTextContent();
        description = contentXML.getElementsByTagName("description").item(0).getTextContent();
        localProperties = (Element) contentXML.getElementsByTagName("Properties").item(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Element getLocalProperties() {
        return localProperties;
    }

    public void setLocalProperties(Element localProperties) {
        this.localProperties = localProperties;
    }

    @Override
    public String toString() {
        return content;
    }
}