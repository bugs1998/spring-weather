package com.github.ekko.springtools.service;

import com.github.ekko.springtools.model.Weather;

import java.util.List;

/**
 * @author cj
 */
public interface EmailService {
    /**
     * 发送短信
     * @return Boolean
     */
    boolean sendSimpleMessage();

    /**
     * 请求天气API并格式化数据
     * @return List<Weather>
     */
    List<Weather> getWeather();
}
