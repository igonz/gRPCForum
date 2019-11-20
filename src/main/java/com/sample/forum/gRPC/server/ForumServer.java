package com.sample.forum.gRPC.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ForumServer {
    private static Logger LOG = LoggerFactory.getLogger(ForumServer.class.getSimpleName());
    private Environment env;
    private List<BindableService> bindableServices;
    private Server server;

    @Autowired
    public ForumServer(Environment env, List<BindableService> bindableServiceList) {
        this.env = env;
        this.bindableServices = bindableServiceList;

        try {
            start();
//            blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            LOG.error("Error Initializing GRPC Server");
        }
    }

    private void start() throws IOException, InterruptedException {
        int port = 443;
//        Integer port = env.getProperty("forum.server.port", Integer.class, 8080);
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        serverBuilder.useTransportSecurity(
                new ClassPathResource("localhost.crt").getInputStream(),
                new ClassPathResource("localhost.key").getInputStream()
        );
        bindableServices.forEach(serverBuilder::addService);
        server = serverBuilder.build().start();
        LOG.info("Server started, listening on " + port);
//        server.awaitTermination(); Uncomment this to keep server running
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
