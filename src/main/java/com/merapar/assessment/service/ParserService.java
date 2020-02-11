package com.merapar.assessment.service;

import com.merapar.assessment.controller.AnalyzeRestController;
import com.merapar.assessment.repository.FileRepository;
import com.merapar.assessment.model.AnalyzesRequest;
import com.merapar.assessment.model.AnalyzesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

@Service
public class ParserService {

    @Autowired
    FileRepository fileRepository;

    private static SAXParser saxParser;
    private static PostsHandler postsHandler;
    private static Logger logger = LoggerFactory.getLogger(AnalyzeRestController.class);


    public ParserService() {
        postsHandler = new PostsHandler();
        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            logger.error("Parser could not be created", e);
        }
    }

    public AnalyzesResult parse(AnalyzesRequest url) throws IOException, SAXException {
        File file = fileRepository.getFile(url);
        InputStream is = new FileInputStream(file);
        saxParser.parse(is, postsHandler);
        return postsHandler.getAnalyzes();
    }
}
