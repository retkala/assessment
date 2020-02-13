package com.merapar.assessment.controller;

import com.merapar.assessment.model.AnalyzesRequest;
import com.merapar.assessment.model.AnalyzesResult;
import com.merapar.assessment.service.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import java.io.*;


@RestController
public class AnalyzeRestController {

    @Autowired
    private ParserService parserService;

    private Logger logger = LoggerFactory.getLogger(AnalyzeRestController.class);

    @RequestMapping(value = "/analyze", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AnalyzesResult> analyzeXml(@RequestBody AnalyzesRequest url) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(parserService.parse(url));
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SAXException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
