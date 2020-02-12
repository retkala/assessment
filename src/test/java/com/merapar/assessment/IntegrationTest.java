package com.merapar.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merapar.assessment.model.AnalyzesResult;
import com.merapar.assessment.repository.FileRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.merapar.assessment.ServiceTest.getTestAnalyzesResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
class IntegrationTest {

    public static final String PATH_TO_TEST_XML = "src/test/resources/file.xml";
    private static final int MOCK_SERVER_PORT = 8000;

    private static ClientAndServer mockServer;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setupMockServer() throws IOException {


        String xml = new String(Files.readAllBytes(Paths.get(PATH_TO_TEST_XML)));

        mockServer = startClientAndServer(MOCK_SERVER_PORT);

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/file.xml"))
                .respond(response().withStatusCode(HttpStatusCode.OK_200.code())
                        .withContentType(MediaType.TEXT_XML)
                        .withBody(xml.getBytes())
                );

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/notFound.xml"))
                .respond(response().withStatusCode(HttpStatusCode.NOT_FOUND_404.code()));
    }

    @AfterAll
    public static void stop() {
        mockServer.stop();
    }

    @Test
    void testOkRequest() throws Exception {
        MvcResult result  = mvc.perform(
                post("http://localhost:8080/analyze")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"url\" : \"http://localhost:8000/file.xml\"}"))
                .andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        AnalyzesResult response = objectMapper.readValue(contentAsString, AnalyzesResult.class);
        assertThat(response.getDetails()).isEqualTo(getTestAnalyzesResult().getDetails());
    }

}
