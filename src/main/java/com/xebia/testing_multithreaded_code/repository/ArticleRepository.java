package com.xebia.testing_multithreaded_code.repository;

import com.xebia.testing_multithreaded_code.model.Article;

import java.util.List;

public class ArticleRepository {

    public List<Article> inventory;

    public ArticleRepository(List<Article> inventory) {
        this.inventory = inventory;
    }

    public Article findById(int articleId) {
        return inventory.stream().filter(article -> article.getId() == articleId)
                .findFirst().orElseThrow(RuntimeException::new);
    }


}
