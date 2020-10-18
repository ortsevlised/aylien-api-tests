Feature: The Stories Endpoint
  The most granular data point we can extract from the News API is a story,
  all other endpoints are aggregations or extrapolations of stories.
  Stories are basically news articles that have been enriched using AYLIEN's machine learning process.

# Real-time monitoring only works when you set sort_by parameter to published_at and sort_direction to desc.
  Scenario: Fresh stories should be available
    When John retrieves the stories with the following details:
      | title  | language | published_at.start | published_at.end | per_page | cursor | sort_by      | sort_direction |
      | corona | en       | NOW-1DAY           | NOW              | 100      | *      | published_at | desc           |
    Then John should have lots of different stories to read
    And John gets a response where stories.title contains corona, stories.language is en,'published_at.start' is present and 'published_at.end' is present
    And the amount of stories matches the amount per +1DAY from the time series endpoint

  Scenario: Analise stories through time
    Given John wants to see the current volume of stories:
      | title  | language | published_at.start | period |
      | corona | en       | NOW-30DAYS         | +1DAY  |
    And John can see a detailed list with the volume per +1DAY

  # The parameter 'text' is used for finding stories whose title or body contains a specific keyword.
  Scenario: Search by keyword
    When John retrieves the stories with the following details:
      | text            | language | published_at.start   | published_at.end     | per_page | cursor |
      | startup, corona | en       | 2020-10-10T15:25:43Z | 2020-10-10T16:25:43Z | 100      | *      |
    Then the selected keywords should appear in either the title or the body

 # This scenario is done in serie as Rest Assured was not done with the idea of concurrent tests in mind
 # a much simpler solution could be implemented in JMeter running all the request in different threads.
   Scenario: Requests should be blocked after the limit is reached
    Given John retrieves a story
    When he reaches his limit
    Then the request is blocked
