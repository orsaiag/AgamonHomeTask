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

**Types of responses:**
POST:
for ex: http://localhost:8080/links
body:
{"url": "https://codesignal.com/learn/course/478/unit/1/practice/6" }
response: 200 status code and response - 48690 (taskId)

GET:
for ex: http://localhost:8080/links/48690

1. Once a url is being process: 200 status code and response - {"status":"IN_PROGRESS","links":null,"error":null}
2. Completed: 200 status code and response -
   {"status":"COMPLETED",
   "links":["<link rel=\"stylesheet\" href=\"/_next/static/css/0f3264c38403e64c.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/9ac4ed065167f79d.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/d43a4866eed1ea9b.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/c00e392e777763b9.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/e6a91d266df211c3.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/f2c285b6c43f087a.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/f979f4534f7ca739.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/28fa6659b99519fa.css\" data-precedence=\"next\">",
   "<link rel=\"stylesheet\" href=\"/_next/static/css/75babc9ee41f137e.css\" data-precedence=\"next\">",
   "<link rel=\"preload\" as=\"script\" fetchpriority=\"low\" href=\"/_next/static/chunks/webpack-25c37cc0662fbdbd.js\">",
   "<link rel=\"preload\" href=\"https://www.googletagmanager.com/gtm.js?id=GTM-WFTVBV78\" as=\"script\">",
   "<link rel=\"icon\" href=\"/favicon.ico\" type=\"image/x-icon\" sizes=\"48x48\">"],"
   error":null}
3. Failed (url wasn't ok): 200 status code and response -
   {"status":"FAILED",
   "links":null,"
   error":"The supplied URL, '123', is malformed. Make sure it is an absolute URL, and starts with 'http://' or '
   https://'. See https://jsoup.org/cookbook/extracting-data/working-with-urls"}
4. Not found : 404 status code and response - {"error":"TaskId wasn't found"}