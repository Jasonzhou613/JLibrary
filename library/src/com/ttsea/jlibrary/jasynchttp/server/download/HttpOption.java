package com.ttsea.jlibrary.jasynchttp.server.download;


import com.ttsea.jlibrary.jasynchttp.server.http.Http;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * 下载信息选项，启动Downloader时，可以通过这个类来配置相关信息 <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/1/6 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 10:05
 */
public class HttpOption {

    private final static int DEFAULT_CONNECTION_TIME_OUT = 15 * 1000;

    //Http request
    private int connectionTimeOut = DEFAULT_CONNECTION_TIME_OUT;
    private long ifModifiedSince = 0;
    private boolean useCache = false;
    private boolean doInput = true;
    //To upload data to a web server, configure the connection for output using setDoOutput(true)
    private boolean doOutput = true;
    private Map<String, String> additionalProperty = null;
    private String requestMethod = Http.Method.GET;
    private SSLSocketFactory sslSocketFactory;

    public HttpOption() {

    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public long getIfModifiedSince() {
        return ifModifiedSince;
    }

    public void setIfModifiedSince(long ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isDoInput() {
        return doInput;
    }

    public void setDoInput(boolean doInput) {
        this.doInput = doInput;
    }

    public boolean isDoOutput() {
        return doOutput;
    }

    public void setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
    }

    public Map<String, String> getAdditionalProperty() {
        return additionalProperty;
    }

    public void setAdditionalProperty(Map<String, String> additionalProperty) {
        this.additionalProperty = additionalProperty;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public String toString() {
        return "HttpOption{" +
                "connectionTimeOut=" + connectionTimeOut +
                ", ifModifiedSince=" + ifModifiedSince +
                ", useCache=" + useCache +
                ", doInput=" + doInput +
                ", doOutput=" + doOutput +
                ", additionalProperty=" + additionalProperty +
                ", requestMethod='" + requestMethod + '\'' +
                ", sslSocketFactory=" + sslSocketFactory +
                '}';
    }
}
