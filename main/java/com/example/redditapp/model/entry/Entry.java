package com.example.redditapp.model.entry;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "entry", strict = false)
public class Entry implements Serializable {

    @Element(name = "content")
    private String content;

    @Element(required = false, name = "author")
    private Author author;

    @Element(name = "id")
    private String id;

    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    public Entry() {

    }

    public Entry(String content, Author author, String title, String updated) {
        this.content = content;
        this.author = author;
        this.title = title;
        this.updated = updated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n\nEntry{" +
                "updated='" + updated + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}' + "\n" +  "--------------------------------------------------------------------------------------------------------- \n";
    }
}
