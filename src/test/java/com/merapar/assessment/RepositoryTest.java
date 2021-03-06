package com.merapar.assessment;


import com.merapar.assessment.model.AnalyzesRequest;
import com.merapar.assessment.repository.FileRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
class RepositoryTest {

    public static final String PATH_TO_TEST_XML = "src/test/resources/file.xml";
    private static final int MOCK_SERVER_PORT = 8000;

    private static ClientAndServer mockServer;

    @Autowired
    private FileRepository fileRepository;

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
    void downloadFileTest() throws FileNotFoundException {
        AnalyzesRequest analyzesRequest = new AnalyzesRequest("http://localhost:8000/file.xml");
        File expectedFile = new File("src/test/resources/file.xml");
        assertThat(fileRepository.getFile(analyzesRequest)).hasSameContentAs(expectedFile);
    }

    @Test
    void fileNotFountTest() {
        AnalyzesRequest analyzesRequest = new AnalyzesRequest("http://localhost:8000/notFound.xml");

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> fileRepository.getFile(analyzesRequest));
        assertThat(exception).isInstanceOf(FileNotFoundException.class);
        assertThat(exception).hasMessage("404 Not Found: [no body]");
    }
}