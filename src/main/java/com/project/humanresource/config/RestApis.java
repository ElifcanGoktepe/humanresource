package com.project.humanresource.config;

public class RestApis {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";
    private static final String BASE_URL = DEV + VERSION;


    public static final String ADDCOMPANY = BASE_URL + "/addcompany";
    public static final String FINDCOMPANYBYNAME = BASE_URL + "/findcompanybyname";
    public static final String FINDCOMPANYBYEMAILADDRESS = BASE_URL + "/findcompanybyemailaddress";
    public static final String FINDCOMPANYBYPHONENUMBER = BASE_URL + "/findcompanybyphonenumber";
    public static final String LISTALLCOMPANY= BASE_URL + "/listallcompany";
    public static final String DELETECOMPANYBYID = BASE_URL + "/deletecompanybyid";
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
    public static final String ADDSHIFT = "/add-shift";
    public static final String REQUESTLEAVE = "/request-leave";
    public static final String REQUESTSHIFT = "/request-shift";
    public static final String ASSIGN_MANAGER = "/assign-manager";
    public static final String ADD_EMPLOYEE = "/add-employee";
}
