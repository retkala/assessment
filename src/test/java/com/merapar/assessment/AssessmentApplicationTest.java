package com.merapar.assessment;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.XmlBody.xml;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@WebAppConfiguration
class AssessmentApplicationTest {

    private Logger logger = LoggerFactory.getLogger(AssessmentApplicationTest.class);
    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.parse("2020-01-06T08:00:00.000");
    private static final String PATH_TO_TEST_XML = "src/test/resources/file.xml";
    private static final int MOCK_SERVER_PORT = 8000;

    private static ClientAndServer mockServer;

    @Autowired
    private MockMvc mvc;

    @Autowired
    RestTemplate restTemplate;

    @BeforeAll
    public static void setupMockServer() throws IOException {


        String xml = new String(Files.readAllBytes(Paths.get(PATH_TO_TEST_XML)));

        mockServer = startClientAndServer(MOCK_SERVER_PORT);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/file.xml"))
                .respond(response().withStatusCode(200)
                        .withBody(xml(xml))
                );
    }

    @AfterAll
    public static void stop() {
        mockServer.stop();
    }

    @Test
    void testAnalyze() throws Exception {
        mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"http://localhost:8000/file.xml\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAnalyzeBadRequest() throws Exception {
        mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"http://localhost:8000/blabla.xml\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testHandler() throws IOException {
        PostsHandler postsHandler = spy(PostsHandler.class);
        when(postsHandler.getCurrentTime()).thenReturn(FIXED_DATE_TIME);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            saxParser.parse(new FileSystemResource(PATH_TO_TEST_XML).getInputStream(), postsHandler);

        } catch (ParserConfigurationException | SAXException e) {
            logger.error("Parsing ", e);
        }

        AnalyzesDetails expectedDetails = new AnalyzesDetails()
                .firstPost(LocalDateTime.parse("2015-07-14T18:39:27.757"))
                .lastPost(LocalDateTime.parse("2015-09-14T12:46:52.053"))
                .totalPosts(80L)
                .totalAcceptedPosts(7L)
                .avgScore(2.975d);

        AnalyzesResult expectedAnalyzesResult = new AnalyzesResult();
        expectedAnalyzesResult.setDetails(expectedDetails);
        expectedAnalyzesResult.setAnalyseDate(FIXED_DATE_TIME);

        assertThat(postsHandler.getAnalyzes()).isEqualTo(expectedAnalyzesResult);
    }
}