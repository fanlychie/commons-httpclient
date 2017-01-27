package org.fanlychie.commons.httpclient;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * HTTP 表单请求
 * Created by fanlychie on 2017/1/27.
 */
public class HttpFormRequest {

    /**
     * HTTP 请求
     */
    private HttpRequestBase request;

    /**
     * 创建一个表单 HTTP 客户端请求
     *
     * @param request
     */
    public HttpFormRequest(HttpRequestBase request) {
        this.request = request;
    }

    /**
     * 普通表单
     *
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest urlencodedForm() {
        return new HttpUrlencodedFormRequest(request);
    }

    /**
     * 支持文件上传的表单
     *
     * @return HttpMultipartFormRequest
     */
    public HttpMultipartFormRequest multipartForm() {
        return new HttpMultipartFormRequest(request);
    }

}