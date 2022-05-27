package edu.skku.cs.personalproject;


public class history_gson {
    public String[] location;
    public String[] pic_name;

    public history_gson(String[] location, String[] pic_name) {
        this.location = location;
        this.pic_name = pic_name;
    }

    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }

    public String[] getPic_name() {
        return pic_name;
    }

    public void setPic_name(String[] pic_name) {
        this.pic_name = pic_name;
    }




}
