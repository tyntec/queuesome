#!/bin/bash

curl -X POST 'http://localhost:8080/queue?name=local&description=show+up+in+person'
curl -X POST 'http://localhost:8080/queue?name=online&description=call'
curl -X POST http://localhost:8080/queue/local -d who=mario
curl -X POST http://localhost:8080/queue/local -d who=luigi
curl -X POST http://localhost:8080/queue/local -d who=peach
curl -X POST http://localhost:8080/queue/local -d who=toad
curl -X POST http://localhost:8080/queue/online -d who=toad
curl -X POST http://localhost:8080/queue/online -d who=mario
curl -X POST http://localhost:8080/queue/online -d who=wario

if type -f pretty-json 2> /dev/null ; then
	curl http://localhost:8080/queue | pretty-json
else
	curl http://localhost:8080/queue
fi

