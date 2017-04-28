package com.example.realgodjj.parking_system.simulation;

import java.util.ArrayList;

public class User {

    private int userId;
    private String userName;
    private String password;
    private String plateNo;
    private String phoneNumber;
    private String email;

    public User(){
    }

    public User(String userName, String password, String plateNo, String phoneNumber, String email) {
        this.userName = userName;
        this.password = password;
        this.plateNo = plateNo;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    //create user database
    public void createUsers(ArrayList<User> users) {
        users.add(new User("MaAonan", "Xiaomage0313", "EA95313", "13439127523", "xiaozhiqiu13@sina.com"));
        users.add(new User("Xiaoming", "123456", "EA12345", "12345678901", "xiaoming@sina.com"));
        users.add(new User("XiaoHong", "654321", "EA54321", "98765432109", "xiaohong@sina.com"));
    }

    //interface of activity_register
    public boolean addUser(ArrayList<User> users, User user){
        users.add(user);
        return true;
    }

    //interface of activity_login
    public int loginUser(ArrayList<User> users, String userName, String password) {
        int i = 0;
        for (User user:users){
            if(userName.equals(user.getUserName()) && password.equals(user.getPassword())) {
                break;
            }
            i++;
        }
        if(i < users.size()) {
            return i + 1;
        } else {
            i = -1;
            return i;
        }
    }

    //interface of check information
    public ArrayList<User> checkUser(ArrayList<User> users, String userName) {
        int i = 0;
        for(User find : users) {
            if(userName.equals(find.getUserName())) {
                return users;
            }
        }
        return null;
    }
}
