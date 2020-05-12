package com.xebia.testing_multithreaded_code;

public class Article {

    private int id;

    private String title;

    private String body;

    private concurrency_multithreading.async.model.ArticleStatus status = concurrency_multithreading.async.model.ArticleStatus.DRAFT;

    private Article(Builder builder) {
        id = builder.id;
        title = builder.title;
        body = builder.body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void publish() {
        if (this.status != concurrency_multithreading.async.model.ArticleStatus.PUBLISH) {
            this.status = concurrency_multithreading.async.model.ArticleStatus.PUBLISH;
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

        public concurrency_multithreading.async.model.Article build() {
            return new concurrency_multithreading.async.model.Article(this);
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

    public concurrency_multithreading.async.model.ArticleStatus getStatus() {
        return status;
    }
}
