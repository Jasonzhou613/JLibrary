package com.ttsea.jlibrary.jasynchttp.db;

import android.database.sqlite.SQLiteDatabase;

import com.ttsea.jlibrary.common.JLog;


/**
 * 数据库用于保存下载信息<br/>
 * Created by Jason on 2015/12/31.
 */
class DBConstants {

    private static final String TAG = "DBConstants";

    /**
     * 只需要在创建数据库的时候调用DBConstants.createTables();<br/>
     * 否则可能不支持一些下载功能
     *
     * @param db SQLiteDatabase
     */
    public static void createTables(SQLiteDatabase db) {
        String[] sqls = new String[]{
                createDOWNLOADINFOTABLE()
        };

        for (int i = 0; i < sqls.length; i++) {
            String sql = sqls[0];
            JLog.d(TAG, "begin create table" + i + "，sql=" + sql);
            db.execSQL(sql);
            JLog.d(TAG, "end create table");
        }
    }

    public static class TableNames {
        /** 多线程断点下载信息表 */
        public static String DOWNLOAD_INFO = "download_info";
    }

    public static class BaseColumn {
        /** ID，主键，自增列 */
        public static final String COLUMN_ID = "_id";

        /** 扩展1 */
        public static final String COLUMN_EXPAND1 = "expand1";

        /** 扩展2 */
        public static final String COLUMN_EXPAND2 = "expand2";
    }

    public static class DOWNLOAD_INFO_COLUMN extends BaseColumn {

        /** 1.每个线程的id */
        public static final String COLUMN_THREAD_ID = "thread_id";

        /** 2.下载地址 */
        public static final String COLUMN_URL = "url";

        /** 3.下载标题，可以用来显示在通知栏里 */
        public static final String COLUMN_TITLE = "title";

        /** 4.下载信息描述，可以用来显示在通知栏里 */
        public static final String COLUMN_DESCRIPTION = "description";

        /** 5.添加时的时间 */
        public static final String COLUMN_ADD_TIMESTAMP = "add_timestamp";

        /** 6.最后修改时间 */
        public static final String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

        /** 7.本地存储路径 */
        public static final String COLUMN_LOCAL_FILE_PATH = "local_file_path";

        /** 8.本地存储的文件名 */
        public static final String COLUMN_LOCAL_FILENAME = "local_filename";

        /** 9.下载文件的类型 */
        public static final String COLUMN_MEDIA_TYPE = "media_type";

        /** 10.提供下载失败原因 */
        public static final String COLUMN_REASON = "reason";

        /** 11.当前下载的状态 */
        public static final String COLUMN_STATUS = "status";

        /** 12.文件总大小 */
        public static final String COLUMN_TOTAL_SIZE_BYTES = "total_size_bytes";

        /** 13.Number of bytes download so far. */
        public static final String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";

        /** 14.开始下载的点 */
        public static final String COLUMN_START_BYTES = "start_bytes";

        /** 15.下载结束的点 */
        public static final String COLUMN_END_BYTES = "end_bytes";

        /** 16.Etag */
        public static final String COLUMN_ETAG = "etag";

    }

    private static String createDOWNLOADINFOTABLE() {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(TableNames.DOWNLOAD_INFO).append(" ( ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_THREAD_ID)// 1.线程id
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_URL)//2.下载地址
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_TITLE)//3.标题
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_DESCRIPTION)//4.描述
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_ADD_TIMESTAMP)//5.添加时间
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_LAST_MODIFIED_TIMESTAMP)//6.最后修改时间
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_LOCAL_FILE_PATH)//7.本地存储路径
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_LOCAL_FILENAME)//8.本地存储的文件名
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_MEDIA_TYPE)//9.文件类型
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_REASON)//10.下载失败原因
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_STATUS)//11.当前状态
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_TOTAL_SIZE_BYTES)//12.文件大小
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_BYTES_DOWNLOADED_SO_FAR)//13.已经下载了的大小
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_START_BYTES)// 14.开始下载的点
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_END_BYTES)//15.下载结束的点
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_ETAG)//16.ETag
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_EXPAND1)
                .append(" TEXT, ")
                .append(DOWNLOAD_INFO_COLUMN.COLUMN_EXPAND2)
                .append(" TEXT").append(" );");

        return sb.toString();
    }
}
