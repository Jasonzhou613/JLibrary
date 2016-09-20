package com.ttsea.jlibrary.sample.jasynchttp;

class Urls {

    public static String SERVER_TEST_IP = "http://test.api.huiweishang.com/";// 测试地址
    public static String UPLOAD_TEST_IP = "http://test.s.huiweishang.com/";// 测试文件上传地址


    public static String getUrl() {
        return SERVER_TEST_IP + INDEX;
    }

    public static String getUploadUrl() {
        return UPLOAD_TEST_IP + UPLOADING_FILE_URL;
    }

    public static String INDEX = "/app/ver2.0.0/index.php";
    public static String UPLOADING_FILE_URL = "/upload/appfiles.v1.php";
    public static String RSID = "RSID=";
}
