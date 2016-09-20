package com.ttsea.jlibrary.jasynchttp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ttsea.jlibrary.common.JLog;
import com.ttsea.jlibrary.jasynchttp.server.download.DownloadFileInfo;
import com.ttsea.jlibrary.jasynchttp.server.download.Downloader;

import java.util.ArrayList;
import java.util.List;


/**
 * 下载数据表操作
 * Created by Jason on 2016/1/5.
 */
public class DownloadOperation {
    private final static String TAG = "DownloadOperation";
    private final static String DOWNLOAD_INFO = DBConstants.TableNames.DOWNLOAD_INFO;

    public static final String THREAD_ID = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_THREAD_ID;
    public static final String _URL = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_URL;
    public static final String TITLE = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_TITLE;
    public static final String DESCRIPTION = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_DESCRIPTION;
    public static final String ADD_TIMESTAMP = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_ADD_TIMESTAMP;
    public static final String LAST_MODIFIED_TIMESTAMP = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_LAST_MODIFIED_TIMESTAMP;
    public static final String LOCAL_FILE_PATH = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_LOCAL_FILE_PATH;
    public static final String LOCAL_FILENAME = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_LOCAL_FILENAME;
    public static final String MEDIA_TYPE = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_MEDIA_TYPE;
    public static final String REASON = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_REASON;
    public static final String STATUS = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_STATUS;
    public static final String TOTAL_SIZE_BYTES = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_TOTAL_SIZE_BYTES;
    public static final String BYTES_DOWNLOADED_SO_FAR = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_BYTES_DOWNLOADED_SO_FAR;
    public static final String START_BYTES = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_START_BYTES;
    public static final String END_BYTES = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_END_BYTES;
    public static final String ETAG = DBConstants.DOWNLOAD_INFO_COLUMN.COLUMN_ETAG;

    /**
     * 获取下载游标
     *
     * @param db  SQLiteDatabase
     * @param url 下载地址
     * @return Cursor 或者 null
     */
    private synchronized static Cursor getDownloaderCursor(SQLiteDatabase db, String url) {
        Cursor cursor = null;

        String selection = "url='" + url + "' order by add_timestamp desc";

        try {
            cursor = db.query(DOWNLOAD_INFO, null, selection, null, null, null, null);

        } catch (Exception e) {
            JLog.d(TAG, "getDownloaderCursor, Exception e:" + e.toString());
            return null;
        }

        return cursor;
    }

    /**
     * 获取某个下载线程的游标
     *
     * @param db       SQLiteDatabase
     * @param url      下载地址
     * @param threadId 线程id
     * @return Cursor 或者 null
     */
    private synchronized static Cursor getThreadCursor(SQLiteDatabase db, String url, String threadId) {
        Cursor cursor = null;

        String selection = "url='" + url + "' AND thread_id='" + threadId + "'";

        try {
            cursor = db.query(DOWNLOAD_INFO, null, selection, null, null, null, null);

        } catch (Exception e) {
            JLog.d(TAG, "getThreadCursor, Exception e:" + e.toString());
            return null;
        }

        return cursor;
    }

    /**
     * 获取该下载任务的线程数
     *
     * @param context 上下文
     * @param url     下载地址
     * @return 线程数或者0
     */
    public synchronized static int getThreadPool(Context context, String url) {
        int threadPool = 0;
        Cursor cursor = null;
        SQLiteDatabase db = DBHelper.getReadableDatabase(context);

        try {
            cursor = getDownloaderCursor(db, url);
            if (cursor != null && cursor.moveToFirst()) {
                threadPool = cursor.getCount();
            }

        } catch (Exception e) {
            JLog.d(TAG, "getThreadPool, Exception e:" + e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return threadPool;
    }

    /**
     * 从数据库中获取Downloader list
     *
     * @param context 上下文
     * @return List of Downloader
     */
    public static List<Downloader> getDownloaders(Context context) {
        List<Downloader> downloaders = new ArrayList<Downloader>();
        Cursor cursor = null;
        SQLiteDatabase db = DBHelper.getReadableDatabase(context);

        try {
            String[] columns = new String[]{_URL};
            cursor = db.query(true, DOWNLOAD_INFO, columns, null, null, null, null, null, null);
            printCursor(cursor);
            if (cursor == null) {
                return downloaders;
            }

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndex(_URL));
                Downloader downloader = new Downloader(context, url);
                downloaders.add(downloader);
            }

        } catch (Exception e) {
            JLog.d(TAG, "getDownloaders, Exception e:" + e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return downloaders;
    }

    /**
     * 获取下载信息
     *
     * @param context 上下文
     * @param url     下载地址
     * @return DownloadFileInfo of list 或者null
     */
    public synchronized static List<DownloadFileInfo> getDownloadInfos(Context context, String url) {
        List<DownloadFileInfo> infos = new ArrayList<DownloadFileInfo>();
        Cursor cursor = null;
        SQLiteDatabase db = DBHelper.getReadableDatabase(context);

        try {
            cursor = getDownloaderCursor(db, url);

            if (cursor == null || cursor.getCount() < 1) {
                return infos;
            }

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String thread_id = cursor.getString(cursor.getColumnIndex(DownloadOperation.THREAD_ID));
                //String url = cursor.getString(cursor.getColumnIndex(DownloadOperation._URL));
                String title = cursor.getString(cursor.getColumnIndex(DownloadOperation.TITLE));
                String description = cursor.getString(cursor.getColumnIndex(DownloadOperation.DESCRIPTION));
                String add_timestamp = cursor.getString(cursor.getColumnIndex(DownloadOperation.ADD_TIMESTAMP));
                String last_modified_timestamp = cursor.getString(cursor.getColumnIndex(DownloadOperation.LAST_MODIFIED_TIMESTAMP));
                String local_file_path = cursor.getString(cursor.getColumnIndex(DownloadOperation.LOCAL_FILE_PATH));
                String local_filename = cursor.getString(cursor.getColumnIndex(DownloadOperation.LOCAL_FILENAME));
                String media_type = cursor.getString(cursor.getColumnIndex(DownloadOperation.MEDIA_TYPE));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadOperation.REASON));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadOperation.STATUS));
                long total_size_bytes = cursor.getLong(cursor.getColumnIndex(DownloadOperation.TOTAL_SIZE_BYTES));
                long bytes_so_far = cursor.getLong(cursor.getColumnIndex(DownloadOperation.BYTES_DOWNLOADED_SO_FAR));
                long start_bytes = cursor.getLong(cursor.getColumnIndex(DownloadOperation.START_BYTES));
                long end_bytes = cursor.getLong(cursor.getColumnIndex(DownloadOperation.END_BYTES));
                String etag = cursor.getString(cursor.getColumnIndex(DownloadOperation.ETAG));

                DownloadFileInfo downloadInfo = new DownloadFileInfo();
                downloadInfo.setThread_id(thread_id);
                downloadInfo.setUrl(url);
                downloadInfo.setTitle(title);
                downloadInfo.setDescription(description);
                downloadInfo.setAdd_timestamp(add_timestamp);
                downloadInfo.setLast_modified_timestamp(last_modified_timestamp);
                downloadInfo.setLocal_file_path(local_file_path);
                downloadInfo.setLocal_filename(local_filename);
                downloadInfo.setMedia_type(media_type);
                downloadInfo.setReason(reason);
                downloadInfo.setStatus(status);
                downloadInfo.setTotal_size_bytes(total_size_bytes);
                downloadInfo.setBytes_so_far(bytes_so_far);
                downloadInfo.setStart_bytes(start_bytes);
                downloadInfo.setEnd_bytes(end_bytes);
                downloadInfo.setEtag(etag);

                JLog.d(TAG, "add downloadInfo in to infos, downloadInfo:" + downloadInfo.toString());

                infos.add(downloadInfo);
            }

        } catch (Exception e) {
            JLog.d(TAG, "getThreadPool, Exception e:" + e.toString());
            return infos;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return infos;
    }

    public synchronized static long insertOrUpdate(Context context, DownloadFileInfo info) {

        if (info == null) {
            JLog.d(TAG, "insertOrUpdate, info is null");
            return 0;
        }

        SQLiteDatabase db = DBHelper.getWritableDatabase(context);
        Cursor cursor = getThreadCursor(db, info.getUrl(), info.getThread_id());

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return update(context, info);
        }
        if (cursor != null) {
            cursor.close();
        }

        long count = 0;

        ContentValues values = new ContentValues();
        values.put(THREAD_ID, info.getThread_id());
        values.put(_URL, info.getUrl());
        values.put(TITLE, info.getTitle());
        values.put(DESCRIPTION, info.getDescription());
        values.put(ADD_TIMESTAMP, info.getAdd_timestamp());
        values.put(LAST_MODIFIED_TIMESTAMP, info.getLast_modified_timestamp());
        values.put(LOCAL_FILENAME, info.getLocal_filename());
        values.put(LOCAL_FILE_PATH, info.getLocal_file_path());
        values.put(MEDIA_TYPE, info.getMedia_type());
        values.put(REASON, info.getReason());
        values.put(STATUS, info.getStatus());
        values.put(TOTAL_SIZE_BYTES, info.getTotal_size_bytes());
        values.put(BYTES_DOWNLOADED_SO_FAR, info.getBytes_so_far());
        values.put(START_BYTES, info.getStart_bytes());
        values.put(END_BYTES, info.getEnd_bytes());
        values.put(ETAG, info.getEtag());

        try {
            count = db.insertOrThrow(DOWNLOAD_INFO, null, values);

        } catch (Exception e) {
            JLog.d(TAG, "add, Exception e:" + e.toString());

        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        JLog.d(TAG, "insertOrUpdate, threadId:" + info.getThread_id() + ", count:" + count);

        return count;
    }

    public synchronized static long update(Context context, DownloadFileInfo info) {
        if (info == null) {
            JLog.d(TAG, "insertOrUpdate, info is null");
            return 0;
        }

        SQLiteDatabase db = DBHelper.getWritableDatabase(context);
        long count = 0;
        String whereClause = "url='" + info.getUrl() + "' and thread_id='"
                + info.getThread_id() + "'";

        ContentValues values = new ContentValues();
//        values.put(THREAD_ID, info.getThread_id());
//        values.put(_URL, info.getUrl());
//        values.put(TITLE, info.getTitle());
//        values.put(DESCRIPTION, info.getDescription());
//        values.put(ADD_TIMESTAMP, info.getAdd_timestamp());
        values.put(LAST_MODIFIED_TIMESTAMP, info.getLast_modified_timestamp());
//        values.put(LOCAL_FILE_PATH, info.getLocal_file_path());
//        values.put(LOCAL_FILENAME, info.getLocal_filename());
        values.put(MEDIA_TYPE, info.getMedia_type());
        values.put(REASON, info.getReason());
        values.put(STATUS, info.getStatus());
        values.put(TOTAL_SIZE_BYTES, info.getTotal_size_bytes());
        values.put(BYTES_DOWNLOADED_SO_FAR, info.getBytes_so_far());
        values.put(START_BYTES, info.getStart_bytes());
        values.put(END_BYTES, info.getEnd_bytes());
        values.put(ETAG, info.getEtag());

        try {
            count = db.update(DOWNLOAD_INFO, values, whereClause, null);

        } catch (Exception e) {
            JLog.e(TAG, "update, Exception e:" + e.toString());

        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        JLog.d(TAG, "update, threadId:" + info.getThread_id() + ", count:" + count);

        return count;
    }

    /**
     * 删除下载记录
     *
     * @param context 上下文
     * @param url     下载地址
     * @return 返回删除的数量
     */
    public synchronized static long deleteRecord(Context context, String url) {

        int count = 0;

        String whereClause = "url='" + url + "'";
        if (url == null || url.length() < 1) {
            whereClause = null;
        }
        SQLiteDatabase db = DBHelper.getWritableDatabase(context);

        try {
            count = db.delete(DOWNLOAD_INFO, whereClause, null);

        } catch (Exception e) {
            JLog.e(TAG, "update, Exception e:" + e.toString());
            count = 0;

        } finally {
            db.close();
        }

        return count;
    }

    /**
     * 删除下载记录
     *
     * @param context 上下文
     * @return 返回删除的数量
     */
    public synchronized static long deleteAllRecord(Context context) {
        return deleteRecord(context, null);
    }

    private synchronized static void printCursor(Cursor c) {
        if (c == null) {
            return;
        }

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int columnCount = c.getColumnCount();
            String columnInfo = "";
            for (int i = 0; i < columnCount; i++) {
                columnInfo = columnInfo + "columnName:" + c.getColumnName(i) + "-columnValue:" + c.getString(i) + ", ";
            }
            JLog.d(TAG, columnInfo);
        }
    }
}