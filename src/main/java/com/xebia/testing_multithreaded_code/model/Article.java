package com.xebia.testing_multithreaded_code.model;

public class Article {

    private int id;

    private String title;

    private String body;

    private ArticleStatus status = ArticleStatus.DRAFT;

    private Article(Builder builder) {
        id = builder.id;
        title = builder.title;
        body = builder.body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void publish() {
        if (this.status != ArticleStatus.PUBLISH) {
            this.status = ArticleStatus.PUBLISH;
        }
    }

    public static final class Builder {
        private int id;
        private String title;
        private String body;

        private Builder() {
        }

        public Builder withId(int val) {
            id = val;
            return this;
        }

        public Builder withTitle(String val) {
            title = val;
            return this;
        }

        public Builder withBody(String val) {
            body = val;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public ArticleStatus getStatus() {
        return status;
    }
}
