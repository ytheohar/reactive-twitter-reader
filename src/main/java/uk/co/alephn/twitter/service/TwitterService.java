package uk.co.alephn.twitter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import uk.co.alephn.twitter.domain.Tweet;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yannis Theocharis (ytheohar@gmail.com)
 */
@Slf4j
@Service
public class TwitterService {

    @Value("${twitter.app.consumer.key}")
    private String consumerKey;

    @Value("${twitter.app.consumer.secret}")
    private String consumerSecret;

    @Value("${twitter.app.token}")
    private String token;

    @Value("${twitter.app.secret}")
    private String secret;

    private final ObjectMapper objectMapper;
    private Client client;

    @Autowired
    public TwitterService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a flux of live tweets containing the specified word
     *
     * @param word the word to match
     * @return the flux of tweets
     */
    public Flux<Tweet> tweetsByWord(String word) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        client = start(queue, word);

        return Flux.generate(sink -> {
            Tweet tweet = readNextTweet(queue);
            sink.next(tweet);
        });
    }

    private Tweet readNextTweet(BlockingQueue<String> queue) {
        try {
            String msg = queue.take();
            log.info("msg: {}", msg);
            return objectMapper.readValue(msg, Tweet.class);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Client start(BlockingQueue<String> queue, String term) {
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Lists.newArrayList("twitterapi", term));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();
        return client;
    }

    @PreDestroy
    public void stop() {
        client.stop();
    }

}
