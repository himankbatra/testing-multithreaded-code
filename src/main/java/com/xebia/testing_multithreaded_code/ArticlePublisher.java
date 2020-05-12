package concurrency_multithreading.async;

import concurrency_multithreading.async.model.Article;
import concurrency_multithreading.async.model.ArticleStatus;

import java.util.List;
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


class ArticleRepository {

    public List<Article> inventory;

    public ArticleRepository(List<Article> inventory) {
        this.inventory = inventory;
    }

    public Article findById(int articleId) {
        return inventory.stream().filter(article -> article.getId() == articleId)
                .findFirst().orElseThrow(RuntimeException::new);
    }


}


class EmailSender {

    public void sendEmail(
            String subject, String body) {
        System.out.println("Successfully Sent mail !! with " + subject + " and body " + body);
    }

}