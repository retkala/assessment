package com.merapar.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;


@RestController
public class AnalyzeRestController {

    @Autowired
    RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(AnalyzeRestController.class);

    @RequestMapping(value = "/analyze", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity analyzeXml(@RequestBody AnalyzesRequest url) {

        PostsHandler postsHandler = new PostsHandler();
        File file;
        try {
            file = restTemplate.execute(URI.create(url.getUrl()), HttpMethod.GET, null, clientHttpResponse -> {
                File ret = File.createTempFile("download", "tmp");
                StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                return ret;
            });
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode())
                    .body(e.getResponseBodyAsString());
        }


        if (file != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser;
            try {
                saxParser = factory.newSAXParser();
                saxParser.parse(new FileInputStream(file), postsHandler);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                logger.error("Error parsing", e);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(postsHandler.getAnalyzes());
    }

}
