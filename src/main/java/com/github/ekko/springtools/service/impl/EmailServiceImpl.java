package com.github.ekko.springtools.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.github.ekko.springtools.config.MassageConfig;
import com.github.ekko.springtools.model.Weather;
import com.github.ekko.springtools.model.Whours;
import com.github.ekko.springtools.service.EmailService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cj
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public boolean sendSimpleMessage() {
        try {
            Credential credential = new Credential(MassageConfig.SECRET_ID,MassageConfig.SECRET_KEY);
            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppid(MassageConfig.APPID);
            req.setSign(MassageConfig.SIGN);
            req.setTemplateID(MassageConfig.TEMPLATE_ID);

            /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
             * 例如+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
            String[] phoneNumbers = {"+8615588888888"};
            req.setPhoneNumberSet(phoneNumbers);

            /* 模板参数: 若无模板参数，则设置为空*/
            String[] templateParams = new String[10];
            List<Weather> weathers = getWeather();
            templateParams[0] = MassageConfig.City;
            templateParams[1] = weathers.get(0).getTem1();
            templateParams[2] = weathers.get(0).getTem2();
            templateParams[3] = weathers.get(0).getTem();

            for (int i = 4;i < templateParams.length;i++){
                templateParams[i] = "-";
            }

            System.out.println(getWeather());
            List<Whours> hours = weathers.get(0).getHours();
            for (int i = 0;i < hours.size();i++){
                if(i < 6){
                    String s = hours.get(i).getDay()+","+hours.get(i).getWea()+","+hours.get(i).getTem()+","+hours.get(i).getWin()+","+hours.get(i).getWinSpeed();
                    templateParams[i+4] = s;
                }
            }
            req.setTemplateParamSet(templateParams);

                for (String weather:templateParams) {
                System.out.println(weather);
            }

            SmsClient client = new SmsClient(credential,"");
            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = client.SendSms(req);

            // 输出 JSON 格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));

            // 可以取出单个值，您可以通过官网接口文档或跳转到 response 对象的定义处查看返回字段的定义
            System.out.println(res.getRequestId());
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        return true;
    }

    @Override
    public List<Weather> getWeather() {
        HttpRequest httpRequest = HttpUtil.createGet("https://www.tianqiapi.com/api?version=v1&" + "appid="+MassageConfig.AppID2+"&appsecret="+MassageConfig.AppSecret+"&cityid="+MassageConfig.CityiID);
        String res = httpRequest.execute().body();
        Object data = JSON.parseObject(res).get("data");
        MassageConfig.City = JSON.parseObject(res).get("city").toString();
        return JSON.parseArray(JSON.toJSONString(data), Weather.class);
    }
}
