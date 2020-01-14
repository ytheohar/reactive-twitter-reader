# reactive-twitter-reader
This is a demo project showing how Webflux of Spring Boot 2 (Spring 5) can be used to develop reactive applications.

To access the Twitter APIs you need to [create a twitter app](https://developer.twitter.com/en/apps) and generate the keys and tokens. 
Fill the 'application.properties' file with this info. 
```
twitter.app.consumer.key=...
twitter.app.consumer.secret=...
twitter.app.token=...
twitter.app.secret=...
```
Run the app with:
```
mvn spring-boot:run
```

Monitor the stream of twits containing the term of your wish with:
```
GET http://localhost:9797/tweets/{term}
```
e.g. 
```
curl -v http://localhost:9797/tweets/Liverpool
```