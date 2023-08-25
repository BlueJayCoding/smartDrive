package com.selfdrive.utill;

import com.selfdrive.exception.SelfDriveException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    public HttpResponse downloadStream(String url, Map<String, String> headers) {

        HttpClient httpClient;
        HttpResponse httpResponse;

        try {

            httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }

            httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 404) {
                String providerError = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
                throw new SelfDriveException("File not found", httpResponse.getStatusLine().getStatusCode(), providerError);
            }
            if (httpResponse.getStatusLine().getStatusCode() == 401) {
                return (HttpResponse) new FileNotFoundException("Not authorized");
            }

            return httpResponse;

        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception e) {
            throw new SelfDriveException("Unknown error", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
