package uk.co.alephn.twitter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import uk.co.alephn.twitter.domain.Tweet;
import uk.co.alephn.twitter.service.TwitterService;

/**
 * @author Yannis Theocharis (ytheohar@gmail.com)
 */
@Slf4j
@RestController
public class TwitterController {

    private final TwitterService twitterService;

    @Autowired
    public TwitterController(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    @GetMapping(value = "/tweets/{term}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Tweet> tweets(@PathVariable String term) {
        return twitterService.tweetsByWord(term);
    }

    @GetMapping(value = "/tweets/{term}/{location}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Tweet> tweetsByLocation(@PathVariable String term, @PathVariable String location) {
        return twitterService.tweetsByWord(term)
                .filter(tweet -> tweet.user().location().contains(location));
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        twitterService.stop();
        log.warn("IOException occurred: {}", e.getMessage());
    }
}
