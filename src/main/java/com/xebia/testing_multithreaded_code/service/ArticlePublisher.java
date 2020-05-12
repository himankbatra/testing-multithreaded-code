package com.xebia.testing_multithreaded_code.service;

import com.xebia.testing_multithreaded_code.model.Article;
import com.xebia.testing_multithreaded_code.model.ArticleStatus;
import com.xebia.testing_multithreaded_code.repository.ArticleRepository;
import com.xebia.testing_multithreaded_code.service.EmailSender;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ArticlePublisher {


    private ArticleRepository articleRepository;

    private EmailSender emailSender;


    public ArticlePublisher(ArticleRepository articleRepository, EmailSender emailSender) {
        this.articleRepository = articleRepository;
        this.emailSender = emailSender;
    }

    public boolean publish(int articleId) {
        Article article = this.articleRepository.findById(articleId);
        article.publish();
        if (article.getStatus() == ArticleStatus.PUBLISH) {
            CompletableFuture.runAsync(() ->
                    {
                        System.out.println("Running in " + Thread.currentThread());
                        randomDelay();
                        this.emailSender.sendEmail("Article Published With Id " + articleId
                                , "Published an article with Article Title " + article.getTitle());
                    }
            )
                    .whenComplete((nil, exception) -> {
                        if (!Objects.isNull(exception)) {
                            System.out.println("exception occurred in sending email" + exception);
                        }
                    });
        }
        return article.getStatus() == ArticleStatus.PUBLISH;
    }

    private void randomDelay() {
        try {
            Random random = new Random();
            int randomNumber = random.nextInt(20) * 100 + 100;
            System.out.println("generating some random delay of " + randomNumber);
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}


