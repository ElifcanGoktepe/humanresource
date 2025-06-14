package com.project.humanresource.config;

public class RestApis {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";
    private static final String BASE_URL = DEV + VERSION;




    public static final String EMPLOYEE = BASE_URL + "/employee";
    public static final String EXPENSES = BASE_URL + "/expenses";
    public static final String LEAVE = BASE_URL + "/leave";
    public static final String PERSONALFILE = BASE_URL + "/personalfile";
    public static final String SHIFT = BASE_URL + "/shift";
    public static final String SHIFTBREAK = BASE_URL + "/shiftbreak";
    public static final String USER = BASE_URL + "/user";


    public static final String CREATEUSER = "/create_user";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String REQUESTLEAVE = "/request-leave";
    public static final String REQUESTSHIFT = "/request-shift";
    public static final String REGISTER = "/register";
    public static final String ADDSHIFT = SHIFT + "/add";
    public static final String UPDATE_SHIFT = SHIFT + "/update";
    public static final String LIST_SHIFT = SHIFT + "/list";
    public static final String ADD_EMPLOYEE = EMPLOYEE + "/add";
    //yeni eklendi

















}
