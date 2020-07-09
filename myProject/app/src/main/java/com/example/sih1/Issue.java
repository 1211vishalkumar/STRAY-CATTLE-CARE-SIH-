package com.example.sih1;

public class Issue {

    public String name, description, image, category, date, time, issueID, issueState;

    public Issue(){

    }

    public Issue(String name, String description, String image, String category, String date, String time, String issueID, String issueState) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.date = date;
        this.time = time;
        this.issueID = issueID;
        this.issueState = issueState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }

    public String getIssueState() {
        return issueState;
    }

    public void setIssueState(String productState) {
        this.issueState = issueState;
    }
}
