package com.merapar.assessment;


import com.google.common.base.Objects;

import java.time.LocalDateTime;

public class AnalyzesDetails {

    private LocalDateTime firstPost;
    private LocalDateTime lastPost;
    private long totalPosts;
    private long totalAcceptedPosts;

    public LocalDateTime getFirstPost() {
        return firstPost;
    }

    public LocalDateTime getLastPost() {
        return lastPost;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public long getTotalAcceptedPosts() {
        return totalAcceptedPosts;
    }

    public double getAvgScore() {
        return avgScore;
    }

    private double avgScore;

    public AnalyzesDetails firstPost(LocalDateTime firstPost) {
        this.firstPost = firstPost;
        return this;
    }

    public AnalyzesDetails lastPost(LocalDateTime lastPost) {
        this.lastPost = lastPost;
        return this;
    }

    public AnalyzesDetails totalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
        return this;
    }

    public AnalyzesDetails totalAcceptedPosts(long totalAcceptedPosts) {
        this.totalAcceptedPosts = totalAcceptedPosts;
        return this;
    }

    public AnalyzesDetails avgScore(double avgScore) {
        this.avgScore = avgScore;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnalyzesDetails)) return false;
        AnalyzesDetails that = (AnalyzesDetails) o;
        return getTotalPosts() == that.getTotalPosts() &&
                getTotalAcceptedPosts() == that.getTotalAcceptedPosts() &&
                Double.compare(that.getAvgScore(), getAvgScore()) == 0 &&
                Objects.equal(getFirstPost(), that.getFirstPost()) &&
                Objects.equal(getLastPost(), that.getLastPost());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getFirstPost(), getLastPost(), getTotalPosts(), getTotalAcceptedPosts(), getAvgScore());
    }

    @Override
    public String toString() {
        return "AnalyzesDetails{" +
                "firstPost=" + firstPost +
                ", lastPost=" + lastPost +
                ", totalPosts=" + totalPosts +
                ", totalAcceptedPosts=" + totalAcceptedPosts +
                ", avgScore=" + avgScore +
                '}';
    }
}
