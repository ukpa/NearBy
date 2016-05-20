package me.unnikrishnanpatel.nearby.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by unnikrishnanpatel on 20/05/16.
 */

public class Place extends RealmObject {
    public Place(){}

    @PrimaryKey
    private String id;

    private String name;

    private int distance;

    private boolean now;

    private String category;

    private String icon_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isNow() {
        return now;
    }

    public void setNow(boolean now) {
        this.now = now;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
