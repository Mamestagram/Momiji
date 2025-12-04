package mames1.net.mamesosu.utils.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


@SuppressWarnings("unused")
public abstract class JsonHttpClient {


    public static JsonNode getJsonNode(HttpURLConnection con) throws IOException {

        try {
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.toString());
            }
        } catch (java.net.SocketTimeoutException e) {
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
