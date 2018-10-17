### Primes Java - a sample implementation of processing a long running task asynchronously

This is a Spring Boot project that uses Spring Rest to implement HTTP endpoint to accept client requests. I have used Jedis for connecting and interacting with Redis cache through redis template within Spring.

* I have installed a redis docker container for redis services. You can pull an image and run a container as below
```
docker pull redis
docker run --name my-redis -p 6379:6379 -d redis
```

Running on a windows machine, I had to connect to the container at a specific IP provided by my virtual box and I have used that in the code.

* Since this project is about handling long running processes, I have used a task queue to asynchronously handle these tasks outside of the regular HTTP request-response cycles. I have used Spring Asynchronous and ThreadPoolExecutor to process tasks in background. The post request is handed off to a task executor and returns a job id that can be used for querying the status.
Unlike the python implementation which implemented this as a distributed queue, here, I have implemented an in-process task queue. I wanted to explore `redisson` package that has distributed queue implementation, but do not have the time.

* I have used Spring Data and RedisTemplate to interface with Redis cache. The post request parameters are used as keys in the cache to keep track of which requests are duplicate.
    * This solution is not ideal for the problem at hand. To get a list of primes, a better solution is to look at the overlapping intervals between the existing keys in cache and the request, and make use of cache for all overlapping intervals. This is complicated and I have not attempted to do it.
    * the keys are created as `primes:start_num:end_num` and checked in cache before dispatching for processing.
    
* I have written a few unit tests for PrimesController class mocking other beans used in the controller.

* The tasks stay in redis cache for a default time period. This has to adjusted and a sensible default has to be set. I have not changed the defaults.


### Running the project, with few examples
This is a spring boot application with maven support.

* Download the repo from github and within the root folder of this repo,

```
# Run redis docker container as mentioned above in a terminal
docker run --name my-redis -p 6379:6379 -d redis

# Start the application
mvn spring-boot:run


# Now send requests to the server with CURL
# using -i option to cURL to see the header which contains HTTP status codes

curl -i -X POST 'localhost:5000/primes?start_num=4&end_num=11'

# Response with status 200 and UUID below

HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 36
Date: Wed, 17 Oct 2018 06:45:16 GMT

966b5112-8a5f-4f79-813b-c91d359e70bb

# Now query the status of the task

$ curl -i -XGET "localhost:8080/result?id=966b5112-8a5f-4f79-813b-c91d359e70bb"

# Response below with status 200 and result

HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 14
Date: Wed, 17 Oct 2018 06:46:29 GMT

2, 3, 5, 7, 11

# Sending the same request return the same id. This is a cache hit and avoids duplicating processing

$ curl -i -XPOST "localhost:8080/primes?start_num=1&end_num=11"
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 36
Date: Wed, 17 Oct 2018 06:48:04 GMT

966b5112-8a5f-4f79-813b-c91d359e70bb

# Now starting a long running job, primes less than 1000000

$ curl -i -XPOST "localhost:8080/primes?start_num=1&end_num=100000000"
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 36
Date: Wed, 17 Oct 2018 06:49:04 GMT

45abcaeb-461f-42dd-9df9-16b533485478

# Response with status code 204, still processing the task ....

$ curl -i -XGET "localhost:8080/result?id=45abcaeb-461f-42dd-9df9-16b533485478"
HTTP/1.1 204
Content-Type: text/plain;charset=UTF-8
Date: Wed, 17 Oct 2018 06:49:47 GMT

```




