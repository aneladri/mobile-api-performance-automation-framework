# API Lab 02 - POST Request Validation

## Objective

Create and validate a POST API request.

## Endpoint

https://httpbin.org/post

## Tasks

1. Create request payload.
2. Send POST request.
3. Validate status code.
4. Validate response body.

## Sample Payload

```json
{
  "name": "MAPAF",
  "type": "training"
}
```

## Validation

* Status Code = 200
* Response contains MAPAF

## Run

```bash
gradle apiTest
```

## Learning Outcome

* POST Requests
* Request Payloads
* Response Validation

