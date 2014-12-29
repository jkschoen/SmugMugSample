Project uses maven and Java 8, so you can build it:

`mvn clean package`

You can run it:

`mvn exec:java -Dexec.mainClass="smugmug.Sample"`

In your console you will see the http request and response, and you should notice
that it is always coming back as `text/html` instead of json.

You just need to enter your API KEY and SECRET in the `smugmug.Sample` class then run it.
