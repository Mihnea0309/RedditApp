package com.example.redditapp.model;

import androidx.annotation.NonNull;

import com.example.redditapp.model.entry.Entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

@Root(name = "feed", strict = false)
public class Feed implements Serializable {
    @Element(required = false, name = "icon")
    private String icon;

    @Element(required = false, name = "id")
    private String id;

    @Element(required = false, name = "title")
    private String title;

    @Element(required = false, name = "updated")
    private String updated;

    @Element(required = false, name = "subtitle")
    private String subtitle;

    @Element(required = false, name = "logo")
    private String logo;

    @ElementList(inline = true, name = "entry")
    private List<Entry> entrys;

    public List<Entry> getEntrys() {
        return entrys;
    }

    public void setEntrys(List<Entry> entrys) {
        this.entrys = entrys;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "Feed: \n [Entryes: \n" + entrys + "]";
    }
}
