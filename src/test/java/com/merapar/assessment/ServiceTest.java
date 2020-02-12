package com.merapar.assessment;

import com.merapar.assessment.model.AnalyzesDetails;
import com.merapar.assessment.model.AnalyzesRequest;
import com.merapar.assessment.model.AnalyzesResult;
import com.merapar.assessment.repository.FileRepository;
import com.merapar.assessment.service.ParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.merapar.assessment.RepositoryTest.PATH_TO_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ServiceTest {

    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.parse("2020-01-06T08:00:00.000");

    @Autowired
    ParserService parserService;

    @MockBean
    FileRepository fileRepository;


    @Test
    public void parseTest() throws IOException, SAXException {
        when(fileRepository.getFile(any(AnalyzesRequest.class))).thenReturn(new File(PATH_TO_TEST_XML));

        AnalyzesResult analyzesResult = parserService.parse(new AnalyzesRequest("url"));
        assertThat(analyzesResult.getDetails()).isEqualTo(getTestAnalyzesResult().getDetails());
    }

    @Test
    public void NoFileParseTest() throws IOException {
        when(fileRepository.getFile(any(AnalyzesRequest.class))).thenThrow(new FileNotFoundException("404 Not Found: [no body]"));

        Exception exception = assertThrows(FileNotFoundException.class, () -> parserService.parse(new AnalyzesRequest("url")));

        assertThat(exception).isInstanceOf(FileNotFoundException.class);
        assertThat(exception).hasMessage("404 Not Found: [no body]");

    }




    public static AnalyzesResult getTestAnalyzesResult() {
        AnalyzesDetails expectedDetails = new AnalyzesDetails()
                .firstPost(LocalDateTime.parse("2015-07-14T18:39:27.757"))
                .lastPost(LocalDateTime.parse("2015-09-14T12:46:52.053"))
                .totalPosts(80L)
                .totalAcceptedPosts(7L)
                .avgScore(2.975d);

        AnalyzesResult expectedAnalyzesResult = new AnalyzesResult();
        expectedAnalyzesResult.setDetails(expectedDetails);
        expectedAnalyzesResult.setAnalyseDate(FIXED_DATE_TIME);
        return expectedAnalyzesResult;
    }

}