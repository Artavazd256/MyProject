package com.avaGo.gameServee.setting;



/**
 * Created by artavzd on 7/14/16.
 */
public class Settings {
    public static final int PROTOCOL_ERROR = 1;
    public static final int ERROR_CODE_USER_NOT_EXISTS = 2; // user not exists
    public static final int ERROR_CODE_NOT_ADD_LIFE = 3;
    public static final int ERROR_PRODUCT_DOESNT_EXIST = 4;
    public static final int ERROR_PRODUCT_COINS = 5;
    public static final int ERROR_PRODUCT_DID_NOT_ADD = 6;
    public static final int WARNING_PRODUCT_NEED_TO_ADD_IN_CODE = 7;
    public static final int ACTION_NOT_COMPLETED = 8;
    public static final long TIME_OF_GET_LIFE_FROM_FRIEND = 60*60*24*1000; // seconds*mints*hours*milliseconds = 86400000 24 hours with milliseconds
    public static final boolean IS_DEBUG = true;
    public static final int MAX_LIFE = 5; // Max life count
    public static final String URL = "%s://%s:%s/%s";
    public static final String URL_REAL = "%s://%s:%s/";
    public static final String IMAGES_DIR = "FB/images";
    public static String HOST;

    public static class DailyBonus {
        public static String day1 = "10Coin";
        public static String day2 = "20Coin";
        public static String day3 = "1spoon";
        public static String day4 = "buster"; // ?????
        public static String day5 = "foreverLife1Hour";
    }

}
