package com.xebia.testing_multithreaded_code;


import com.xebia.testing_multithreaded_code.model.Article;
import com.xebia.testing_multithreaded_code.repository.ArticleRepository;
import com.xebia.testing_multithreaded_code.service.EmailSender;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArticlePublisherTests {


    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private ArticlePublisher articlePublisher;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void should_throw_exception_while_publishing_an_article() {
        //Arrange
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("some exception");
        when(this.articleRepository.findById(1)).thenThrow(new RuntimeException("some exception"));

        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        fail("this should not be called");
    }

    @Ignore
    @Test
    public void should_publish_an_article() {
        //Arrange
        Article article = Article.newBuilder()
                .withBody("learning how to test multithreaded java code")
                .withId(1)
                .withTitle("title").build();
        when(this.articleRepository.findById(1)).thenReturn(article);
        doNothing().when(this.emailSender).sendEmail(anyString(), anyString());

        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        assertThat(publish).isTrue();
        verify(this.articleRepository).findById(1);
        verify(this.emailSender, times(1)).sendEmail("Article Published With Id " + 1
                , "Published an article with Article Title " + "title");
        verifyNoMoreInteractions(this.articleRepository, this.emailSender);
    }


    @Test
    public void should_fail_to_verify_sending_email_method_call_as_some_random_delay_is_there() {
        //Arrange
        Article article = Article.newBuilder()
                .withBody("learning how to test multithreaded java code")
                .withId(1)
                .withTitle("title").build();
        when(this.articleRepository.findById(1)).thenReturn(article);
        // unnecessary stubbing as we are verifying sendEmail with never.
        // doNothing().when(this.emailSender).sendEmail(anyString(), anyString());

        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        assertThat(publish).isTrue();
        verify(this.articleRepository).findById(1);
        verify(this.emailSender, never()).sendEmail("Article Published With Id " + 1
                , "Published an article with Article Title " + "title");
        verifyNoMoreInteractions(this.articleRepository, this.emailSender);
    }


    @Test
    public void should_publish_an_article_using_count_down_latch_to_fix() throws InterruptedException {
        //Arrange
        Article article = Article.newBuilder()
                .withBody("learning how to test multithreaded java code")
                .withId(1)
                .withTitle("title").build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        when(this.articleRepository.findById(1)).thenReturn(article);
        doAnswer(invocationOnMock -> {
            System.out.println("Sending mail !!!");
            countDownLatch.countDown();
            return null;
        }).when(this.emailSender).sendEmail(anyString(), anyString());

        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        assertThat(publish).isTrue();
        verify(this.articleRepository).findById(1);
        countDownLatch.await();
        verify(this.emailSender).sendEmail("Article Published With Id " + 1
                , "Published an article with Article Title " + "title");
        verifyNoMoreInteractions(this.articleRepository, this.emailSender);
    }


    @Test
    public void should_publish_an_article_using_cyclic_barrier_to_fix() throws BrokenBarrierException, InterruptedException {
        //Arrange
        Article article = Article.newBuilder()
                .withBody("learning how to test multithreaded java code")
                .withId(1)
                .withTitle("title").build();

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> System.out.println("Barrier opening"));
        when(this.articleRepository.findById(1)).thenReturn(article);
        doAnswer(invocationOnMock -> {
            System.out.println("sending mail !!!");
            cyclicBarrier.await();
            return null;
        }).when(this.emailSender).sendEmail(anyString(), anyString());
        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        assertThat(publish).isTrue();
        verify(this.articleRepository).findById(1);
        cyclicBarrier.await();
        verify(this.emailSender).sendEmail("Article Published With Id " + 1
                , "Published an article with Article Title " + "title");
        verifyNoMoreInteractions(this.articleRepository, this.emailSender);
    }

    @Test
    public void should_publish_an_article_if_sending_mail_throws_exception_using_count_down_latch_to_fix() throws InterruptedException {
        //Arrange
        Article article = Article.newBuilder()
                .withBody("learning how to test multithreaded java code")
                .withId(1)
                .withTitle("title").build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        when(this.articleRepository.findById(1)).thenReturn(article);
        doAnswer(invocationOnMock -> {
            countDownLatch.countDown();
            throw new RuntimeException();
        }).when(this.emailSender).sendEmail(anyString(), anyString());

        //Act
        boolean publish = this.articlePublisher.publish(1);

        //Assert
        assertThat(publish).isTrue();
        verify(this.articleRepository).findById(1);
        countDownLatch.await();
        verify(this.emailSender).sendEmail("Article Published With Id " + 1
                , "Published an article with Article Title " + "title");
        verifyNoMoreInteractions(this.articleRepository, this.emailSender);
    }

}