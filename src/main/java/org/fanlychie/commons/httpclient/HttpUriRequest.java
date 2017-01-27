package org.fanlychie.commons.httpclient;

import org.apache.http.Consts;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import java.util.Map;

/**
 * HTTP URI 地址请求
 * Created by fanlychie on 2017/1/26.
 */
public class HttpUriRequest extends HttpClientRequest {

    /**
     * URI 构建器
     */
    private URIBuilder uriBuilder;

    /**
     * 创建一个 URI HTTP 客户端请求
     *
     * @param request HttpRequestBase
     */
    public HttpUriRequest(HttpRequestBase request) {
        super(request);
        this.uriBuilder = new URIBuilder().setCharset(Consts.UTF_8);
    }

    /**
     * 添加请求参数
     *
     * @param name  参数名称
     * @param value 参数的值
     * @return HttpUriRequest
     */
    public HttpUriRequest addParameter(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param nameValues 请求参数的名称和值对照表
     * @return HttpUriRequest
     */
    public HttpUriRequest addParameters(Map nameValues) {
        nameValues.forEach((k, v) -> addParameter(k.toString(), v.toString()));
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
        if (uriBuilder.getQueryParams().size() > 0) {
            request.setURI(uriBuilder.setPath(request.getURI().toString()).build());
            if (log.isDebugEnabled()) {
                log.debug(request.toString());
            }
        }
    }

}