package org.fanlychie.commons.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.Map;

/**
 * HTTP 普通表单请求
 * Created by fanlychie on 2017/1/26.
 */
public class HttpUrlencodedFormRequest extends HttpClientRequest {

    /**
     * 实体构建器
     */
    private EntityBuilder builder;

    /**
     * 创建一个普通表单的 HTTP 客户端请求
     *
     * @param request HttpRequestBase
     */
    public HttpUrlencodedFormRequest(HttpRequestBase request) {
        super(request);
        this.builder = EntityBuilder.create()
                .setContentEncoding("UTF-8")
                .setParameters(new LinkedList<NameValuePair>())
                .setContentType(ContentTypeConstant.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 添加请求参数
     *
     * @param name  参数名称
     * @param value 参数的值
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest addParameter(String name, String value) {
        builder.getParameters().add(new BasicNameValuePair(name, value));
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param nameValues 请求参数的名称和值对照表
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest addParameters(Map nameValues) {
        nameValues.forEach((k, v) -> addParameter(k.toString(), v.toString()));
        return this;
    }

    /**
     * 添加 JSON 请求参数
     *
     * @param content JSON 内容
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest addJSONParameter(String content) {
        builder.setContentType(ContentTypeConstant.APPLICATION_JSON);
        builder.setText(content);
        return this;
    }

    /**
     * 添加 XML 请求参数
     *
     * @param content XML 内容
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest addXMLParameter(String content) {
        builder.setContentType(ContentTypeConstant.APPLICATION_XML);
        builder.setText(content);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param content 文本内容
     * @return HttpUrlencodedFormRequest
     */
    public HttpUrlencodedFormRequest addParameter(String content) {
        builder.setText(content);
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
            String displayText = request + " " + entity + "\n";
            if (builder.getParameters() != null) {
                displayText += builder.getParameters();
            } else {
                displayText += builder.getText();
            }
            log.debug(displayText);
        }
        if (entity.getContentLength() > 0) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
    }

}