package info.raphaelnova.movieservice.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * Interceptor to add gRPC context values, such as trace IDs, to outgoing calls.
 * This allows tracing information to be sent with the gRPC requests.
 */
//@Component
public class OutgoingGrpcInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
                String traceId = "abcde";
                if (traceId != null) {
                    headers.put(Metadata.Key.of(
                        "Trace-ID", Metadata.ASCII_STRING_MARSHALLER),
                        traceId);
                }
                super.start(responseListener, headers);
            }
        };
    }
}
