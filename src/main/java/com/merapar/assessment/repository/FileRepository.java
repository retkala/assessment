package com.merapar.assessment.repository;

import com.merapar.assessment.model.AnalyzesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

@Repository
public class FileRepository {

    @Autowired
    RestTemplate restTemplate;

    public File getFile(AnalyzesRequest url) {
        File file;
        file = restTemplate.execute(URI.create(url.getUrl()), HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
        return file;
    }
}
