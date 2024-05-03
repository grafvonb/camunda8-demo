package com.boczek.c8demo.orchestrator.two;

import io.camunda.common.auth.*;
import io.camunda.operate.CamundaOperateClient;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class OrchestratorTwoConfiguration {

    @Bean
    public WebClient.Builder webClientBuilder() {
        // https://www.baeldung.com/spring-webflux-timeout
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(1)) // time we wait to receive a response after sending a request
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // a period within which a connection between a client and a server must be established (30 seconds default)
                .option(ChannelOption.SO_KEEPALIVE, true); // configure the keep-alive option, which will send TCP check probes when the connection is idle
                /*
                .option(EpollChannelOption.TCP_KEEPIDLE, 300) // keep-alive checks to probe after 5 minutes of being idle, at 60 seconds intervals. We also set the maximum number of probes before the connection dropping to 8
                .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                .option(EpollChannelOption.TCP_KEEPCNT, 8);
                 */

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public CamundaOperateClient operateClient() {

        SimpleConfig config = new SimpleConfig();
        config.addProduct(Product.OPERATE, new SimpleCredential("http://localhost:8081", "demo", "demo"));

        Authentication sa = SimpleAuthentication.builder()
                .withSimpleConfig(config)
                .build();

        return CamundaOperateClient.builder()
                .operateUrl("http://localhost:8081")
                .authentication(sa)
                .setup().build();
    }
}
