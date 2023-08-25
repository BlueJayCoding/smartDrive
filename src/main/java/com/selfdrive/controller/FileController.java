package com.selfdrive.controller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import com.selfdrive.exception.SelfDriveException;
import com.selfdrive.utill.HttpUtil;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static com.mashape.unirest.http.Unirest.get;


@RestController
@RequestMapping("/files")
@Data
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {


    @Value("${selfdrive.authorization:}")
    private String authorization;

    @Value("${selfdrive.base.url:}")
    private String baseUrl;


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userDetails() {
        try {
            String url = baseUrl + "/me";
            Map<String, String> inputHeaders = new HashMap<>();
            inputHeaders.put("accept", "application/json");
            inputHeaders.put("Authorization", getAuthorization());
            HttpResponse<String> response = get(url).headers(inputHeaders).asString();
            System.out.println(response.getStatus());
            System.out.println(response.getBody());
            return response.getBody();
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

    }

    //Download file by path
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(@RequestParam("path") String path, HttpServletResponse res) {
        try {
            String url = getBaseUrl() + "/files?path=" + path;
            inputForDownload(res, url);
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Download a file with id
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void downloadFileWithID(@PathVariable String id, HttpServletResponse res) {
        try {
            String url = getBaseUrl() + "/files/" + id;
            inputForDownload(res, url);
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void inputForDownload(HttpServletResponse res, String url) throws IOException {
        Map<String, String> inputHeaders = new HashMap<>();
        inputHeaders.put("accept", "application/octet-stream");
        inputHeaders.put("Authorization", getAuthorization());
        HttpUtil httpUtil = new HttpUtil();
        org.apache.http.HttpResponse httpResponse = httpUtil.downloadStream(url, inputHeaders);
        InputStream is = httpResponse.getEntity().getContent();
        res.addHeader("Content-Disposition", httpResponse.getHeaders("Content-Disposition")[0].getValue());
        res.addHeader("Content-Type", "application/octet-stream");
        IOUtils.copyLarge(is, res.getOutputStream());
    }

    // Delete a file by path
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void deleteFile(@RequestParam("path") String path) {
        try {
            String url = getBaseUrl() + "/files?path=" + path;
            Map<String, Object> inputQuery = new HashMap<>();
            inputForDelete(inputQuery, url);

        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteFileWithId(@PathVariable String id) {
        try {
            Map<String, Object> inputQuery = new HashMap<>();
            String url = getBaseUrl() + "/files/" + id;
            inputForDelete(inputQuery, url);
            System.out.println("file deleted");

        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

    }

    public boolean isError(HttpResponse response) {
        return response.getStatus() >= 400;
    }

    private void inputForDelete(Map<String, Object> inputQuery, String url) throws UnirestException {
        Map<String, String> inputHeaders = new HashMap<>();
        inputHeaders.put("accept", "application/json");
        inputHeaders.put("Authorization", getAuthorization());
        HttpResponse<String> response = Unirest.delete(url).queryString(inputQuery).headers(inputHeaders).asString();
        if (isError(response)) {
            throw new SelfDriveException("Something went wrong.", response.getStatus());
        }
        System.out.println(response.getStatus());
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void uploadFile(@RequestParam("mimeType") String mimeType, @RequestParam("path") String path,
                           HttpServletRequest request) {

        try {
            String url = getBaseUrl() + "/files";
            Map<String, String> inputHeaders = new HashMap<>();
            Map<String, Object> inputQuery = new HashMap<>();
            inputHeaders.put("accept", "application/octet-stream");
            inputHeaders.put("Authorization", getAuthorization());
            inputQuery.put("path", path);
            inputQuery.put("mimeType", mimeType);
            Part part = request.getPart("file");
            InputStream is = part.getInputStream();
            MultipartBody multipartBody = Unirest.post(url).queryString(inputQuery).headers(inputHeaders).field("file"
                    , is, ContentType.parse(part.getContentType()), part.getName());
            HttpResponse<String> response =
                    multipartBody.mode(HttpMultipartMode.BROWSER_COMPATIBLE.toString()).asString();
            System.out.println(response.getBody());
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
