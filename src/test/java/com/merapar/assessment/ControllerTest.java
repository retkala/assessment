package com.merapar.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.merapar.assessment.model.AnalyzesRequest;
import com.merapar.assessment.model.AnalyzesResult;
import com.merapar.assessment.service.ParserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.xml.sax.SAXException;

import java.io.IOException;

import static com.merapar.assessment.ServiceTest.getTestAnalyzesResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class ControllerTest {

    @MockBean
    ParserService parserService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testAnalyzeOkRequest() throws Exception {

        when(parserService.parse(any(AnalyzesRequest.class))).thenReturn(getTestAnalyzesResult());

        MvcResult result  = mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"okUrl\"}"))
                .andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        AnalyzesResult response = objectMapper.readValue(contentAsString, AnalyzesResult.class);
        assertThat(response).isEqualTo(getTestAnalyzesResult());
    }

    @Test
    void testAnalyzeFileNotFound() throws Exception {

        when(parserService.parse(any(AnalyzesRequest.class))).thenThrow(new IOException("file not found"));

        mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"badUrl\"}"))
                .andExpect(status().isNotFound());

    }

    @Test
    void testAnalyzeParserException() throws Exception {

        when(parserService.parse(any(AnalyzesRequest.class))).thenThrow(new SAXException());

        mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"badUrl\"}"))
                .andExpect(status().isInternalServerError());


    }

}