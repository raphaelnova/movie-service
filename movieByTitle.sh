#!/bin/bash

movie="${1:-What Dreams May Come}"
grpcurl -d '{"title": "'''"$movie"'''"}' \
        -rpc-header "traceId: trace-from-call" \
        -proto src/main/protobuf/movie.proto \
        -plaintext \
        localhost:9090 \
        MovieGraph.GetMovieByTitle
