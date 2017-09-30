#!/bin/bash

curl -X POST 'http://localhost:8080/queue?name=local&description=show+up+in+person'
curl -X POST 'http://localhost:8080/queue?name=online&description=call'
curl -X POST http://localhost:8080/queue/local?who=mario
curl -X POST http://localhost:8080/queue/local?who=luigi
curl -X POST http://localhost:8080/queue/local?who=peach
curl -X POST http://localhost:8080/queue/local?who=toad
curl -X POST http://localhost:8080/queue/online?who=toad
curl -X POST http://localhost:8080/queue/online?who=mario

if type -f pretty-json 2> /dev/null ; then
	curl http://localhost:8080/queue | pretty-json
else
	curl http://localhost:8080/queue
fi

