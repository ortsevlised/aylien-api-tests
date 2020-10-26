@This
Feature: The Stories Endpoint
  The most granular data point we can extract from the News API is a story,
  all other endpoints are aggregations or extrapolations of stories.
  Stories are basically news articles that have been enriched using AYLIEN's machine learning process.

  # The News API gathers articles in near real-time, and stores and indexes them along with metadata and enrichments, which you can search over.
  # Real-time monitoring only works when you set sort_by parameter to published_at and sort_direction to desc.
  # Newly published stories will be pulled every five minutes, to ensure you are only getting the most recent publications, rather than a repeat of what has come before.
  Scenario: Fresh stories should be available
    When John retrieves all the stories with the following details:
      | language | published_at.start | published_at.end | per_page | cursor | sort_by      | sort_direction |
      | en       | NOW-6MINUTES       | NOW              | 100      | *      | published_at | desc           |
    And John gets a response where stories.title notNullValue,'published_at.start' notNullValue and 'published_at.end' notNullValue

  Scenario: Analise stories through time
    Given John wants to see the current volume of stories:
      | title  | language | published_at.start | period |
      | corona | en       | NOW-30DAYS         | +1DAY  |
    And John can see a detailed list with the volume per +1DAY

  # The parameter 'text' is used for finding stories whose title or body contains a specific keyword.
  Scenario: Search by keyword
    When John retrieves all the stories with the following details:
      | text            | language | published_at.start   | published_at.end     | per_page | cursor |
      | startup, corona | en       | 2020-10-10T15:25:43Z | 2020-10-10T16:25:43Z | 100      | *      |
    Then the selected keywords should appear in either the title or the body

 # Rest assured processes the requests in sequential order as is not intended for performance testing.
 # A much simpler solution could be implemented in JMeter running all the request in different threads at the same time,
 # therefore reducing the execution time.
  Scenario: Requests should be blocked after the limit is reached
    Given John retrieves a story
    When he reaches his limit
    Then the request is blocked
