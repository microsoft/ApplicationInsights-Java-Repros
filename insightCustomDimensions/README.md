* A single controller that receives HTTP requests
* A request filter similar to the one you have in your code using the @Order tag.
* Inside the filter, create your custom span and apply the filter as you currently do in your code.
* Collect a dummy custom property that is expected to be added to your request.
* Enable App Insights Java Agent 3.7.2
* Use the same applicationinsights.json file that you have on your current Application.
* Even if this dummy code does not reproduce the issue, it will help our engineering team to
  understand this scenario.

To Reproduce
Submitting a PR with an example reproducing the issue
in [this repository](https://github.com/microsoft/ApplicationInsights-Java-Repros) would make it
easier for the Application Insight maintainers to help you. Before doing this, you have to fork this
repository.

## Sample Test App

* 5 Filter added
  * `SpanInitializationFilter`: init the span
  * `OidLoggingFilter`: read the OID from JWT Baerer if exists
  * `CountryLoggingFilter`: read the dbName from URL like /api/v1/{dbName}/company
  * `HttpQueryLoggingFilter`: read the Query-Parameter
  * `RawBodyLoggingFilter`: read the body of POST and PUT
* Endpoints:
  * GET /api/v1/:dbName/company (i.e. :dbName=germany)
    * Response:
      ```    
        {
        "id": 1758202622283,
        "name": "germany-Name",
        "street": "germany-Street",
        "postalCode": "germany-PostalCode",
        "city": "germany-City"
        },
        {
        "id": 1758202622283,
        "name": "germany-Name",
        "street": "germany-Street",
        "postalCode": "germany-PostalCode",
        "city": "germany-City"
        }
        ]
      ```
  * GET /api/v1/:dbName/company/:id (i.e :dbName=germany, id=123)
    * Response:
      ```
        {
        "id": 111,
        "name": "germany-Name",
        "street": "germany-Street",
        "postalCode": "germany-PostalCode",
        "city": "germany-City"
        }
      ```

  * POST/PUT /api/v1/:dbName/company (i.e :dbName=germany)
    * Request/Response (sample)
      ```        
        {
        "id": 111,
        "name": "germany-Name",
        "street": "germany-Street",
        "postalCode": "germany-PostalCode",
        "city": "germany-City"
        }
      ```