# movie-service
Experimenting with Neo4J, Reactive Spring and gRPC

This project uses the movie-graph database from Neo4J guides. It needs to be
loaded manually after creating the neo4j docker container inside dev/neo4j.
Run `docker compose up -d`, wait until it's loaded and open the Neo4J browser
at [localhost:7474](http://localhost:7474/browser/). Under `Guides` you'll find
a `:guide movie-graph` example that gives you a script to populate the database.

With Neo4J up all you need is `mvn spring-boot:run`. This will autogenerate the
gRPC stubs, so your IDE may complain about missing files if you open this project
without generating them (if you don't want to run it now, generate them with
`mvn protobuf:generate`)

The only protobuf file can be found under src/main/protobuf. I didn't bother with
using another repo until I had another service using the same interface. When that
happens, it's recommended to have a different git repository saved here as a
module. Code will be generated under target/generated-sources/protobuf and they
will throw a lot of warnings, so if your IDE let you disable warnings for this
folder then you probably should do it.

There are two services implementing the same gRPC service, a `MovieGraphGrpcService`
and a `ReactorMovieGraphGrpcService`. The first is a vanilla Spring gRPC implementation
that exposes gRPC's `StreamObserver`, and the second uses Salesforce's `reactor-grpc`
which bridges gRPC and Reactor exposing only Reactor interfaces (but the project
is unmantained). Only the first is active with `@Service`.

At the root of the project there's a script `movieByTitle.sh` showing how to
interact with the gRPC service. You'll need `grpcurl` installed in order to
send gRPC requests via CLI. Another nice tool is `Wireshark`, since you'll
need to intercept binary packets if you ever need to debug the requests. It doesn't
parse gRPC packets by default though, so you'll need to configure it (see resources
below).

Context propagation doesn't work between gRPC and Reactor using `reactor-grpc` and
I'm not satisfied with having to manually propagate it on every endpoint, hence
the multiple annotations and aop and configs all trying different approaches.


## Resources

- Spring gRPC Guide - https://docs.spring.io/spring-grpc/reference/index.html
- Analyzing gRPC messages using Wireshark - https://grpc.io/blog/wireshark/
- Mapstruct SPI implementation for protocol buffers - https://github.com/entur/mapstruct-spi-protobuf
- Protobuf Maven Plugin - https://github.com/ascopes/protobuf-maven-plugin

