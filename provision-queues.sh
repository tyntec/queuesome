#!/bin/bash

curl -X POST 'http://localhost:8080/queue?name=12016768942&description=show+up+in+person'
curl -X POST 'http://localhost:8080/queue?name=online&description=call'
curl -X POST http://localhost:8080/queue/12016768942 -d who=mario
curl -X POST http://localhost:8080/queue/12016768942 -d who=luigi
curl -X POST http://localhost:8080/queue/12016768942 -d who=peach
curl -X POST http://localhost:8080/queue/12016768942 -d who=toad
curl -X POST http://localhost:8080/queue/online -d who=toad
curl -X POST http://localhost:8080/queue/online -d who=mario
curl -X POST http://localhost:8080/queue/online -d who=wario

if type -f pretty-json 2> /dev/null ; then
	curl http://localhost:8080/queue | pretty-json
else
	curl http://localhost:8080/queue
fi

