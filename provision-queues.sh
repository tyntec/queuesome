#!/bin/bash

curl -X POST 'http://localhost:8080/queue?name=test&description=test+1'
curl -X POST 'http://localhost:8080/queue?name=test2&description=test+2'
curl -X POST http://localhost:8080/queue/test?who=mario
curl -X POST http://localhost:8080/queue/test?who=luigi
curl -X POST http://localhost:8080/queue/test?who=peach
curl -X POST http://localhost:8080/queue/test?who=toad
curl -X POST http://localhost:8080/queue/test2?who=toad
curl -X POST http://localhost:8080/queue/test2?who=mario

if type -f pretty-json 2> /dev/null ; then
	curl -X POST http://localhost:8080/queue | pretty-json
else
	curl -X POST http://localhost:8080/queue
fi

