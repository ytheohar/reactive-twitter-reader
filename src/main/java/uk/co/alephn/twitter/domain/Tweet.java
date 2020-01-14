package uk.co.alephn.twitter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Yannis Theocharis (ytheohar@gmail.com)
 */
@Data
@Accessors(fluent = true)
public class Tweet {

    @JsonProperty
    private String createdAt;

    @JsonProperty
    private String text;

    @JsonProperty
    private User user;

    @Data
    @Accessors(fluent = true)
    public static class User {

        @JsonProperty
        private String name;

        @JsonProperty
        private String screenName;

        @JsonProperty
        private String location;
    }
}
