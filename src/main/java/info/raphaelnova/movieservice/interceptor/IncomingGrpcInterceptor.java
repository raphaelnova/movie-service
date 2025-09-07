package info.raphaelnova.movieservice.interceptor;

import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor to propagate gRPC context values, such as trace IDs.
 * This allows tracing information to be available in the gRPC service methods.
 */
@Slf4j
@Component
@GlobalServerInterceptor
public class IncomingGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String traceId = headers.get(Metadata.Key.of(
            "traceId", Metadata.ASCII_STRING_MARSHALLER));

        log.info("Intercepting gRPC call with traceId: {}", traceId);

        if (traceId == null) {
            traceId = "trace-header-was-null";
        }

        Context contextWithTraceId = Context.current()
            .withValue(Context.key("traceId"), traceId);

        return Contexts.interceptCall(contextWithTraceId, call, headers, next);
    }
}