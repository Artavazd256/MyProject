package com.avaGo.gameServee.setting;



/**
 * Created by artavzd on 7/14/16.
 */
public class Settings {
    public static final int PROTOCOL_ERROR = 1;
    public static final int ERROR_CODE_USER_NOT_EXISTS = 2; // user not exists
    public static final int ERROR_CODE_NOT_ADD_LIFE = 3;
    public static final long TIME_OF_GET_LIFE_FROM_FRIEND = 60*60*24*1000; // seconds*mints*hours*milliseconds = 86400000 24 hours with milliseconds
    public static final boolean IS_DEBUG = true;
    public static final int MAX_LIFE = 5; // Max life count
    public static final String URL = "%s://%s:%s/%s";
    public static final String URL_REAL = "%s://%s:%s/";
    public static final String IMAGES_DIR = "FB/images";
    public static String HOST;
}
