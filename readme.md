# Aylien QA Challenge

## Tests were created using Serenity BDD, Cucumber and RestAssured

  ### Checking for fresh stories:
  - Performed a keyword search using a time period of the last 5 minutes, simulating what's explained in the documentation for 'Real-time' monitoring:
  
 *The News API gathers articles in near real-time, and stores and indexes them along with metadata and enrichments, which you can search over.
   Real-time monitoring only works when you set sort_by parameter to published_at and sort_direction to desc.
   Newly published stories will be pulled every five minutes, to ensure you are only getting the most recent publications, rather than a repeat of what has come before.*
   
  - If what it was intended to be tested is that the api returns the latest news available from its sources, what could be done is a request to the external source for the latest news and then compare it to the Story endpoint using the source as a parameter filter.
  This is not recommended though as we would be using an external resource, it would be best to mock it.
        
 ### Comparing results through time:
  - Information about how many stories were published was gathered using the Timeseries endpoint.
  
*AYLIEN’s Timeseries endpoint empowers us to do this. With this endpoint, we can track changes in quantitative values contained in stories over time. This information can be anything from mentions of a topic or entities, sentiment about a topic, or the volume of stories published by a source, to name but a few. 
It is also much faster to pull aggregate Timeseries data then it is to query the stories endpoint and return individual stories in batches and subsequently process them to measure the occurrences of entities, sentiment etc. contained in each article. 
In addition to retrieving stories themselves, the Time Series endpoint provides the count of stories published over time matching your parameters.*

**I would suggest a dashboard monitoring this endpoint instead a test, it could be implemented collecting the data in influxDB and displaying it in grafana.**
 ### Search by Keywords:
 - A set of keywords were passed on the 'text' field of the request and then a check was done to validate that the keywords appear either in the title or the body of the story.
 *The parameter 'text' is used for finding stories whose title or body contains a specific keyword.*
 
 ### Validate a rate-limiting work accurately on the /stories endpoint:
 - A check was done to validate that the headers display the right xrate-limits and a 429 error is returned after exceeding the limit
 - There's a JMeter test for this as well if you prefer on /resources/jmeterTest.jmx
 
 
 ## The tests report can be seen on this jenkins pipeline:
**Click on the 'Serenity' link on the sidebar to see the report**
 - url: http://35.228.80.223/job/Aylien%20API/ 
 - user: aylien
 - password: !@£$rewq
 
**Otherwise, test can be manually ran using the runner in CucumberTestSuite.class from any IDE, or using maven
 as mvn clean verify**
