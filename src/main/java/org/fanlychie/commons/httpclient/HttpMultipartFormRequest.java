package org.fanlychie.commons.httpclient;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.InputStream;

/**
 * HTTP 支持文件上传的表单请求
 * Created by fanlychie on 2017/1/27.
 */
public class HttpMultipartFormRequest extends HttpClientRequest {

    /**
     * 实体构建器
     */
    private MultipartEntityBuilder builder;

    /**
     * 创建一个支持文件上传表单的 HTTP 客户端请求
     *
     * @param request HttpRequestBase
     */
    public HttpMultipartFormRequest(HttpRequestBase request) {
        super(request);
        this.builder = MultipartEntityBuilder.create()
                .setCharset(Consts.UTF_8)
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    /**
     * 添加请求参数
     *
     * @param name 参数名称
     * @param file 文件对象
     * @return HttpMultipartFormRequest
     */
    public HttpMultipartFormRequest addParameter(String name, File file) {
        builder.addPart(name, new FileBody(file));
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param name  参数名称
     * @param value 参数的值
     * @return HttpMultipartFormRequest
     */
    public HttpMultipartFormRequest addParameter(String name, String value) {
        builder.addPart(name, new StringBody(value, ContentTypeConstant.TEXT_HTML));
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param name     参数的名称
     * @param in       输入流参数
     * @param filename 文件名称参数
     * @return HttpMultipartFormRequest
     */
    public HttpMultipartFormRequest addParameter(String name, InputStream in, String filename) {
        builder.addPart(name, new InputStreamBody(in, filename));
        return this;
    }

    /**
     * 发起请求前的处理工作
     *
     * @param request HttpRequestBase
     * @throws Exception
     */
    @Override
    protected void preHandle(HttpRequestBase request) throws Exception {
        HttpEntity entity = builder.build();
        if (log.isDebugEnabled()) {
            log.debug(request + " " + entity.getContentType());
        }
        if (entity.getContentLength() > 0) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
    }

}