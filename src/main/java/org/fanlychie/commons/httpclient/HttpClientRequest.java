package org.fanlychie.commons.httpclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.ssl.SSLContextBuilder;
import org.fanlychie.commons.httpclient.exception.RuntimeCastException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * HTTP 客户端请求基类
 * Created by fanlychie on 2017/1/26.
 */
public abstract class HttpClientRequest {

    /**
     * HTTP 请求
     */
    private HttpRequestBase request;

    /**
     * 代理端口
     */
    private int proxyPort;

    /**
     * 代理主机
     */
    private String proxyHost;

    /**
     * 代理模式 ( http 或 https )
     */
    private String proxySchema;

    /**
     * 失败重试的次数, 默认重试3次
     */
    private int retryTimes = 3;

    /**
     * 读取超时时间, 默认3分钟
     */
    private int readTimeout = 3 * 60 * 1000;

    /**
     * 连接超时时间, 默认30秒
     */
    private int connectTimeout = 30 * 1000;

    /**
     * 响应内容编码
     */
    private String contentEncoding = "UTF-8";

    /**
     * 日志
     */
    protected Log log = LogFactory.getLog(getClass());

    /**
     * 创建一个 HTTP 客户端请求
     *
     * @param request HttpRequestBase
     */
    public HttpClientRequest(HttpRequestBase request) {
        this.request = request;
    }

    /**
     * 发起请求前的处理工作
     *
     * @param request HttpRequestBase
     * @throws Exception
     */
    protected abstract void preHandle(HttpRequestBase request) throws Exception;

    /**
     * 执行请求
     *
     * @param consumer (请求结果的状态码, 请求结果的文本内容)
     */
    public void execute(BiConsumer<Integer, String> consumer) {
        try (CloseableHttpClient client = buildHttpClient()) {
            // 发起请求前的处理工作
            preHandle(request);
            // 执行请求
            HttpResponse response = client.execute(request);
            // 状态码
            int statusCode = response.getStatusLine().getStatusCode();
            // 响应内容
            String responseText = null;
            // 请求失败
            if (statusCode != HttpStatus.SC_OK) {
                // 尝试获取友好的提示消息内容
                responseText = getSimpleResponseText(statusCode);
            }
            // 处理响应内容
            if (responseText == null) {
                // 响应内容
                StringBuilder responseTextBuilder = new StringBuilder();
                // 响应内容读取对象
                Reader reader = new InputStreamReader(response.getEntity().getContent(), contentEncoding);
                // 读取响应内容
                try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseTextBuilder.append(line);
                    }
                }
                responseText = responseTextBuilder.toString();
            }
            if (log.isDebugEnabled()) {
                log.debug("statusCode: " + statusCode + ", responseText: " + responseText);
            }
            if (consumer != null) {
                // Consumer
                consumer.accept(statusCode, responseText);
            }
        } catch (Exception e) {
            throw new RuntimeCastException(e);
        }
    }

    /**
     * 添加请求头参数
     *
     * @param name  参数名称
     * @param value 参数的值
     * @return HttpClientRequest
     */
    public HttpClientRequest addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }

    /**
     * 添加请求头参数
     *
     * @param headers 请求头参数表
     * @return HttpClientRequest
     */
    public HttpClientRequest addHeaders(Map headers) {
        headers.forEach((k, v) -> request.addHeader(k.toString(), v.toString()));
        return this;
    }

    /**
     * 设置失败重试次数
     *
     * @param retryTimes 失败重试次数
     * @return HttpClientRequest
     */
    public HttpClientRequest setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    /**
     * 设置读取超时时间, 单位毫秒, 默认3分钟
     *
     * @param readTimeout 读取超时时间
     * @return HttpClientRequest
     */
    public HttpClientRequest setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * 设置连接超时时间, 单位毫秒, 默认30秒
     *
     * @param connectTimeout 连接超时时间
     * @return HttpClientRequest
     */
    public HttpClientRequest setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 设置 HTTP 代理
     *
     * @param proxyHost 代理主机
     * @param proxyPort 代理端口
     * @return HttpClientRequest
     */
    public HttpClientRequest setHttpProxy(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxySchema = "http";
        return this;
    }

    /**
     * 设置 HTTPS 代理
     *
     * @param proxyHost 代理主机
     * @param proxyPort 代理端口
     * @return HttpClientRequest
     */
    public HttpClientRequest setHttpsProxy(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxySchema = "https";
        return this;
    }

    /**
     * 设置响应内容字符集编码
     *
     * @param contentEncoding 响应内容字符集编码
     * @return HttpClientRequest
     */
    public HttpClientRequest setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    /**
     * 构建 HTTP 客户端对象
     *
     * @return CloseableHttpClient
     * @throws Exception
     */
    private CloseableHttpClient buildHttpClient() throws Exception {
        // 请求配置
        Builder config = RequestConfig.custom()
                // 设置连接超时
                .setConnectTimeout(connectTimeout)
                // 设置读取超时
                .setSocketTimeout(readTimeout);
        // 设置代理
        if (proxyHost != null) {
            config.setProxy(new HttpHost(proxyHost, proxyPort, proxySchema));
        }
        // 客户端构建器
        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                // 设置请求配置
                .setDefaultRequestConfig(config.build())
                // 设置失败重试
                .setRetryHandler(new StandardHttpRequestRetryHandler(retryTimes, true));
        // 是否使用SSL协议链接
        if (request.getURI().getScheme().equalsIgnoreCase("https")) {
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(
                    new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        // 信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build()));
        }
        return clientBuilder.build();
    }

    /**
     * 简易的响应内容
     *
     * @param statusCode 状态码值
     * @return 返回简易易懂的的文本内容
     */
    private String getSimpleResponseText(int statusCode) {
        switch (statusCode) {
            // Bad Request
            case HttpStatus.SC_BAD_REQUEST:
                return "400 Bad Request - 语义有误，服务器无法理解请求。";
            // Forbidden
            case HttpStatus.SC_FORBIDDEN:
                return "403 Forbidden - 没有权限，服务器拒绝处理请求。";
            // Not Found
            case HttpStatus.SC_NOT_FOUND:
                return "404 Not Found - 请求的资源在服务器端未被发现。";
            // Method Not Allowed
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                return "405 Method Not Allowed - 请求的资源中指定的方法被禁用。";
            // Unsupported Media Type
            case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE:
                return "415 Unsupported Media Type - 不支持的媒体类型，请检查Content-Type是否正确。";
            // Internal Server Error
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                return "500 Internal Server Error - 服务器端处理请求发生错误。";
            // Service Unavailable
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                return "503 Service Unavailable - 服务器正在维护或负载过重未能应答。";
            // Others
            default:
                return null;
        }
    }

}