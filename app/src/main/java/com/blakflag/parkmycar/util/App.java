package com.blakflag.parkmycar.util;

import com.blakflag.parkmycar.model.User;

/**
 * Created by Amit on 8/12/2017.
 */

public  class App {
    public final static String USER_DB="PARKING_USER/User_db";
    public final static String RENT_PARKINFO_DB="RENT_USER/parkinfo";
    public final static String PARKING_REQUSET_DB="RENT_USER/PARKING_REQUSET";
    public static final String PARKING_HISTORY_DB = "History/RentHistoryDB";
    public static User user;
    public static void initUser()
    {
        user=new User();
    }

    public static String CN_USER="user";
    public static String CN_RENT_USER="rent_user";
}
