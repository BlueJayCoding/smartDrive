package com.selfdrive.controller;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.selfdrive.exception.SelfDriveException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;

import static com.mashape.unirest.http.Unirest.get;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class FolderController {

    @Value("${selfdrive.authorization:}")
    private String authorization;

    @Value("${selfdrive.base.url:}")
    private String baseUrl;

    @RequestMapping(value = "/folders", method = RequestMethod.GET)
    public void getFoldersContents() throws UnirestException {

        try {
            String url = baseUrl + "/folders/contents";
            input(url);
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/folder/{id}", method = RequestMethod.GET)
    public void getFolderContents(@PathVariable String id) throws UnirestException {

        try {
            String url = baseUrl + "/folders/" + id + "/contents";
            input(url);
        } catch (SelfDriveException se) {
            throw se;
        } catch (Exception exception) {
            throw new SelfDriveException("Failed at controller", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isError(HttpResponse response) {
        return response.getStatus() >= 400;
    }

    private void input(String url) throws UnirestException {

        Map<String, String> inputHeaders = new HashMap<>();
        Map<String, Object> inputQuery = new HashMap<>();
        inputHeaders.put("accept", "application/json");
        inputHeaders.put("Authorization", authorization);
        inputQuery.put("path", "/");
        HttpResponse<JsonNode> response =
                get(url).headers(inputHeaders).queryString(inputQuery).asJson();
        if (isError(response)) {
            throw new SelfDriveException("Something went wrong.", response.getStatus());
        }
        System.out.println(response.getStatus());
        System.out.println(response.getBody());

    }


}
