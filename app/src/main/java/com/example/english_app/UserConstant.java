package com.example.english_app;

public class UserConstant {
    public static boolean isLogined = false;
    private static String userID = "account";
    private static String userPassword = "";
    private static String userEmail = "email";

    public static void SetUserData(String userID, String userPassword, String userEmail){
        UserConstant.userID = userID;
        UserConstant.userPassword = userPassword;
        UserConstant.userEmail = userEmail;
        isLogined = true;
    }
    public static void ClearUserData(){
        userID = "account name";
        userPassword = "";
        userEmail = "email";
        isLogined = false;
    }


    public static String GetUserID(){
        return userID;
    }
    public static String GetUserEmail(){
        return userEmail;
    }
}
