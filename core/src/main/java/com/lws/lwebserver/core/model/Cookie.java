package com.lws.lwebserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zl on 2019/03/01.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cookie {
    private String key;
    private String value;
}
