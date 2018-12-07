import org.apache.commons.lang.RandomStringUtils;
import utils.HttpClient;

import java.io.IOException;
import java.text.ParseException;

public class SmsSend {

    public static void main(String[] args) throws IOException, ParseException {
        HttpClient httpClient = new HttpClient("http://127.0.0.1:7788/sms/sendSms");

        String numeric = RandomStringUtils.randomNumeric(4);
        httpClient.addParameter("phoneNumbers","13383118288");
        httpClient.addParameter("signName","品位优雅购物");
        httpClient.addParameter("templateCode","SMS_130926832");
        httpClient.addParameter("templateParam","{\"code\":\""+numeric+"\"}");
        httpClient.post();
        System.out.println(numeric);
        System.out.println(httpClient.getStatusCode());
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
