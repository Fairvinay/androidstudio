package com.jwell.suite.model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

public abstract class StringRequestBody extends RequestBody {

    public static RequestBody create(String content) {
        MediaType contentType = MediaType.parse("application/json");
        Charset charset = StandardCharsets.UTF_8;
        if (contentType != null) {
            charset = contentType.charset();
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
                contentType = MediaType.parse(contentType + "; charset=utf-8");
            }
        }
        byte[] bytes = content.getBytes(charset);
        return create(contentType, bytes);
    }

}
