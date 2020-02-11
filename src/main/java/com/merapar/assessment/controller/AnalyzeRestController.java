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
    ParserService parserService;

    private Logger logger = LoggerFactory.getLogger(AnalyzeRestController.class);

    @RequestMapping(value = "/analyze", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AnalyzesResult> analyzeXml(@RequestBody AnalyzesRequest url) {
        AnalyzesResult result = null;
        try {
            result = parserService.parse(url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (SAXException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
