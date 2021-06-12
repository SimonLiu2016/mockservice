### Mock Service

An easy to use service to mock REST and SOAP services.
Suitable for integration testing and similar purposes.

> Important: SOAP support is very simplistic and limited.
It requires you to provide valid SOAP envelope as a response.
There is NO support for WSDL/XSD.

#
### Where to start

Refer to README in `src/main/webapp` for instructions on how to build web application.

#
### Route Response

Response can contain:
- HTTP 1.1 response in textual format with or without HTTP head
(JSON or XML body)
- HTTP 1.1 request in textual format **with** HTTP head
(JSON or XML body)

> See HTTP request and response formats here
https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages

**RESPONSE**: Parser looks for `HTTP/1.1` at the beginning of a line
to start reading response.
Then an empty line should go indicating head ended
(head is optional for response part).
Then goes the body if not blank.

Anything that goes before head start (`HTTP/1.1`)
is considered a response body.

**REQUEST** (optional): Parser looks for `HTTP/1.1` at the end of a line
to start reading request.
Then headers may go.
Then an empty line should go indicating head ended
(head is mandatory for request part).
Then goes the body if not blank.

If request is present it would be executed asynchronously
after the response was sent.

Simple response (only body):

    {"id": 1, name": "Johnny 5"}

Response with head:

    HTTP/1.1 400
    Cache-Control: no-cache
        
    {"code": "E000394", "message": "Internal error"}

Response with head + request:

    HTTP/1.1 202
    
    {
        "status": "PROCESSING",
        "id": "${item_id}"
    }
    POST /store/cart/item/${item_id} HTTP/1.1
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
    
    {
        "status": "PROCESSED",
        "id": "${item_id}"
    }

#
### Route Alt (Alternative)

Alt allow you to create alternative responses for the same path.

To select a particular alt send **Mock-Alt** header in HTTP request
or you may enable **Random Alt** in **Settings** (or even **Go Quantum** ;).

Multiple **Mock-Alt** headers supported per HTTP request.
Each header should define exactly one alternative.

Header format:

    request-mapping/alt
    
Example:

    Mock-Alt: api-v1-item-{id}/invalid_format

In the example above if you call an endpoint `/api/v1/item/{id}`
then the Route with the `invalid_format` Alt would match.

#
### Predefined variables

You can use predefined variables in Response, those would be substituted
with their values each time an endpoint is fetched.

List of predefined variables:

- `${sequence}` - sequence of integers starting from 1
- `${random_int}` - random integer between 1 and 10_000
- `${random_int:min:max}` - random integer between `min` and `max`
- `${random_long}` - random long between 1 and 1_000_000_000_000_000L
- `${random_long:min:max}` - random long between `min` and `max`
- `${random_uuid}` - random UUID
- `${random_string}` - a string of 20 random characters in `[a-z]`
- `${random_string:min:max}` - a string of `min` to `max` random characters in `[a-z]`
- `${random_date}` - random date in yyyy-MM-dd format
- `${random_timestamp}` - random timestamp in yyyy-MM-dd HH:mm:ss.SSS format
- `${current_date}` - current date in yyyy-MM-dd format
- `${current_timestamp}` - current timestamp in yyyy-MM-dd HH:mm:ss.SSS format.
- `${enum:str1:str2:...}` - a random one of given arguments (may be useful to represent enum values)

#
### Provided variables

You can use variables which are provided on a per-request basis.

Format:

    ${var_name}
    ${var_name:default_value}

Variables are collected from the following sources:

1. Path variables (`/api/v1/account/{id}`).
2. Request parameters (`/api/v1/account?id=1`).
3. Request payload (if it is in JSON).
All fields of the JSON would be collected and made available as variables
(preserving hierarchy, see example below).
4. **Mock-Variable** header (see section below).

Example of request payload:

    {
        "key1": "value 1",
        "key2": {
            "key1": "other value 1"
        }
    }

The following variables would be available:

    ${key1}
    ${key2.key1}

#
### Mock-Variable header

Multiple **Mock-Variable** headers supported per HTTP request.
Each header defines exactly one variable.

Header format:

    request-mapping/variable_name/value
    
Example:

    Mock-Variable: api-v1-item-{id}/item_name/Chips

In the example above if you call an endpoint `/api/v1/item/{id}`
a variable `item_name` with the value `Chips` would be available.