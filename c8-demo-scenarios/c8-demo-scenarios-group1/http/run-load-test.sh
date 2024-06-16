#!/bin/zsh

ab -n 1000 -c 1 -p payload.json -T application/json http://localhost:8100/process/start-reactive
