package org.fanlychie.commons.httpclient;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;

/**
 * 内容类型常量
 * Created by fanlychie on 2017/1/27.
 */
public interface ContentTypeConstant {

    /**
     * text/html utf-8 content type
     */
    ContentType TEXT_HTML = ContentType.create("text/html", Consts.UTF_8);

    /**
     * application/json utf-8 content type
     */
    ContentType APPLICATION_JSON = ContentType.create("application/json", Consts.UTF_8);

    /**
     * application/x-www-form-urlencoded utf-8 content type
     */
    ContentType APPLICATION_FORM_URLENCODED = ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8);

    /**
     * application/xml utf-8 content type
     */
    ContentType APPLICATION_XML = ContentType.create("application/xml", Consts.UTF_8);

}