package com.example.english_app;

// 출처: https://ilbbang.tistory.com/52 [일빵의 티스토리]
// PHP 파일 주소 목록을 설정합니다.
public class Constant {
    // 아래는 각자 환경에 맞게 설정해야 합니다.
    private static final String BASE_PATH = "https://circlezero.loca.lt/DictionaryApp/";

    private static final String CRUD_BASE_PATH = "CRUD/";
    public static final String CREATE = BASE_PATH + CRUD_BASE_PATH + "dbInput.php";
    public static final String READ = BASE_PATH + CRUD_BASE_PATH + "dbRead.php";
    public static final String UPDATE = BASE_PATH + CRUD_BASE_PATH + "dbUpdate.php";
    public static final String DELETE = BASE_PATH + CRUD_BASE_PATH + "dbDelete.php";
    public static final String IDCLEAR = BASE_PATH + CRUD_BASE_PATH + "dbIdClear.php";

    private static final String USER_MANAGEMENT_PATH = "UserManagement/";
    public static final String LOGIN = BASE_PATH + USER_MANAGEMENT_PATH + "Login.php";
    public static final String SIGNIN = BASE_PATH + USER_MANAGEMENT_PATH + "Signin.php";

    public static final String GET_METHOD = "GET";
    static final String POST_METHOD = "POST";
}