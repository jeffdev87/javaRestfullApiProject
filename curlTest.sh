#!/bin/bash

#curl -H "Content-Type: text/plain" -X POST --data-binary "@./screenplay.txt" localhost:8080/myapp/moviescript/script/full1
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/100
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/2
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/1/1
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/1/2

#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/100
curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/2
#THREEPIO
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/2/2
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/1/3

#curl -H "Content-Type: text/plain" -X POST --data-binary "@./screenplay.txt" localhost:8080/myapp/moviescript/script/full5
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/settings/1
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters
#curl -H "Content-Type: text/plain" -X GET localhost:8080/myapp/moviescript/characters/1
