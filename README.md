# AgamonHomeTask

In order to test this project, you will need the next steps:
In the command line - I'm writing this commands for windows (so if you have any other you will need an adjustments):

1. git clone https://github.com/orsaiag/AgamonHomeTask.git
2. cd AgamonHomeTask
3. ./gradlew clean build
4. java -jar build/libs/AgamonHomeTask-1.0-SNAPSHOT-all.jar server config.yml

Using cli: (windows)
curl -X POST "http://localhost:8080/links" -H "Content-Type: application/json" --data-raw "{\"url\": \"https://github.com/orsaiag/AgamonHomeTask\"}"
curl -X GET "http://localhost:8080/links/123456"


If I had more time:
1. I would use a real DB and not in memory maps.
2. Make this service work not just locally.

Api responses: 
https://github.com/orsaiag/AgamonHomeTask/blob/main/ResponseTypes.md