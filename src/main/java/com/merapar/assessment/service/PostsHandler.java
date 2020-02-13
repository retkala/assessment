package com.merapar.assessment.service;

import com.merapar.assessment.model.AnalyzesDetails;
import com.merapar.assessment.model.AnalyzesResult;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDateTime;

public class PostsHandler extends DefaultHandler {

    private static final String POSTS = "posts";
    private static final String POST = "row";
    private static final String ACCEPTED = "AcceptedAnswerId";
    private static final String SCORE = "Score";
    private static final String CREATION_DATE = "CreationDate";

    private static long sum;
    private static long numberOfPosts;
    private static LocalDateTime earliestDate;
    private static LocalDateTime latestDate;
    private static long numberOfAccepted;

    @Override
    public void startDocument() {
        sum = 0;
        numberOfAccepted = 0;
        numberOfPosts = 0;
        earliestDate = null;
        latestDate = null;

    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) {
        switch (qName) {
            case POSTS:
                sum = 0;
                numberOfAccepted = 0;
                numberOfPosts = 0;
                earliestDate = null;
                latestDate = null;
                break;
            case POST:
                numberOfPosts++;
                String accetpedValue = attr.getValue(ACCEPTED);
                if (accetpedValue != null) {
                    numberOfAccepted++;
                }
                String score = attr.getValue(SCORE);
                if (score != null) {
                    sum += Integer.parseInt(score);
                }
                String creationDate = attr.getValue(CREATION_DATE);
                if (creationDate != null) {
                    if (earliestDate == null || earliestDate.isAfter(LocalDateTime.parse(creationDate))) {
                        earliestDate = LocalDateTime.parse(creationDate);
                    }
                    if (latestDate == null || latestDate.isBefore(LocalDateTime.parse(creationDate))) {
                        latestDate = LocalDateTime.parse(creationDate);
                    }
                }
        }
    }

    public double getAverageScore() {
        if (numberOfPosts != 0) {
            return (double) sum / numberOfPosts;
        }
        return 0;
    }

    public static long getNumberOfPosts() {
        return numberOfPosts;
    }

    public static LocalDateTime getEarliestDate() {
        return earliestDate;
    }

    public static LocalDateTime getLatestDate() {
        return latestDate;
    }

    public static long getNumberOfAccepted() {
        return numberOfAccepted;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public AnalyzesResult getAnalyzes() {
        AnalyzesResult analyzesResult = new AnalyzesResult();
        analyzesResult.setAnalyseDate(getCurrentTime());
        AnalyzesDetails details = new AnalyzesDetails()
                .avgScore(getAverageScore())
                .totalAcceptedPosts(getNumberOfAccepted())
                .lastPost(getLatestDate())
                .firstPost(getEarliestDate())
                .totalPosts(getNumberOfPosts());
        analyzesResult.setDetails(details);
        return analyzesResult;
    }
}