package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;

public class User {
    private int id;
    private String userName;
    private String password;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdated;
    private String lastUpdatedBy;

    private static ObservableList<User> users = FXCollections.observableArrayList();

    private static User activeUser;

    public User(int id, String userName, String password, Timestamp createDate, String createdBy, Timestamp lastUpdated, String lastUpdatedBy){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setId(int id){
        this.id = id;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setCreateDate(Timestamp createDate){
        this.createDate = createDate;
    }

    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    public void setLastUpdated(Timestamp lastUpdated){
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdatedBy(String lastUpdatedBy){
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public static void addUser(User user){
        users.add(user);
    }

    public static void setActiveUser(User user){
        activeUser = user;
    }

    public int getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public Timestamp getCreateDate(){
        return createDate;
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public Timestamp getLastUpdated(){
        return lastUpdated;
    }

    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }

    public static ObservableList<User> getUsers(){
        return users;
    }

    public static User getActiveUser(){
        return activeUser;
    }

    @Override
    public String toString(){
        return (userName);
    }
}
