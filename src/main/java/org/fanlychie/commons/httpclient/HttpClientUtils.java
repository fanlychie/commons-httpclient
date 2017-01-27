package org.fanlychie.commons.httpclient;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

/**
 * HTTP 客户端请求工具类
 * Created by fanlychie on 2017/1/27.
 */
public final class HttpClientUtils {

    /**
     * 私有化
     */
    private HttpClientUtils() {

    }

    /**
     * HTTP GET 请求
     *
     * @param url 请求地址
     * @return 返回一个 HTTP URI 请求对象
     */
    public static HttpUriRequest get(String url) {
        return new HttpUriRequest(new HttpGet(url));
    }

    /**
     * HTTP PUT 请求
     *
     * @param url 请求地址
     * @return 返回一个 HTTP 表单请求对象
     */
    public static HttpFormRequest put(String url) {
        return new HttpFormRequest(new HttpPut(url));
    }

    /**
     * HTTP POST 请求
     *
     * @param url 请求地址
     * @return 返回一个 HTTP 表单请求对象
     */
    public static HttpFormRequest post(String url) {
        return new HttpFormRequest(new HttpPost(url));
    }

    /**
     * HTTP DELETE 请求
     *
     * @param url 请求地址
     * @return 返回一个 HTTP URI 请求对象
     */
    public static HttpUriRequest delete(String url) {
        return new HttpUriRequest(new HttpDelete(url));
    }

}