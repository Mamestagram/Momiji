package mames1.net.mamesosu.support.beatmap;

import mames1.net.mamesosu.object.Setting;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

public abstract class RequestSender {

    // wip: サーバー側のリクエスト受け取る部分のセキュリティをあげる
    public static void sendHttpRequest(String category, int id, int status)  {

        Setting setting = new Setting();

        String url = "https://api." + setting.getDomain() + "/v1/set_status_change?key=" + setting.getSecretKey() + "&";

        try {

            URL uri = new URL(url + category + "=" + id + "&status=" + status);
            HttpsURLConnection connection = (HttpsURLConnection) uri.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.getInputStream();

        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
