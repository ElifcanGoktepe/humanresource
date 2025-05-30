package com.project.humanresource.config;

public class RestApis {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";
    private static final String BASE_URL = DEV + VERSION;

    public static final String ADMIN = "/admin";

    public static final String ADDDERPARTAMENT = "/department/add";
    public static final String FINDDEPARTMENTBYID = "/department/findById";
    public static final String LISTALLDEPARTMENTS = "/department/listAll";
    public static final String DELETEDEPARTMENTBYID = "/department/delete";
    public static final String FINDDEPARTMENTBYCODE = "/department/findByCode";

    public static final String PENDINGAPPLICATIONS= BASE_URL + "/pendingapplications";
    public static final String UPDATEAPPLICATIONSTATUSBYID = BASE_URL + "/updateapplicationstatusbyid";
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
    public static final String REGISTER = "/register";
    public static final String ADD_EMPLOYEE = "/add-employee";

    public static final String ADDCOMPANY =BASE_URL + "/company/add";
    public static final String FINDCOMPANYBYNAME =BASE_URL + "/company/findByName";
    public static final String FINDCOMPANYBYEMAILADDRESS = BASE_URL +"/company/findByEmail";
    public static final String FINDCOMPANYBYPHONENUMBER = BASE_URL +"/company/findByPhoneNumber";
    public static final String LISTALLCOMPANY = BASE_URL +"/company/listAll";
    public static final String DELETECOMPANYBYID =BASE_URL + "/company/delete";
    public static final String MYCOMPANY = BASE_URL + "/mycompany";
    public static final String ADDCOMPANYBRANCH =BASE_URL + "/companyBranch/add";
    public static final String LISTALLCOMPANYBRANCH =BASE_URL + "/companyBranch/listAll";
    public static final String DELETECOMPANYBRANCHBYID = BASE_URL +"/companyBranch/delete";
    public static final String FINDCOMPANYBRANCHBYADDRESS =BASE_URL + "/companyBranch/findByAddress";
    public static final String FINDCOMPANYBRANCHBYEMAILADDRESS =BASE_URL + "/companyBranch/findByEmail";
    public static final String FINDCOMPANYBRANCHBYPHONENUMBER = BASE_URL +"/companyBranch/findByPhoneNumber";


    public static final String COMPANY = BASE_URL + "/company";

    public static final String ADD_COMPANY = COMPANY + "/add";
    public static final String GET_ALL_COMPANIES = COMPANY + "/listAll";
    public static final String GET_COMPANY_BY_ID = COMPANY + "/findById/{id}";
    public static final String UPDATE_COMPANY = COMPANY + "/update/{id}";
    public static final String DELETE_COMPANY = COMPANY + "/delete/{id}";
    public static final String SEARCH_COMPANY_BY_NAME = COMPANY + "/findByName";
    public static final String UPDATE_COMPANY_EMAIL = COMPANY + "/updateEmail/{id}";
    public static final String MY_COMPANY = COMPANY + "/myCompany";
}
