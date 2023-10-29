package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
public class MainServerApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MainServerApp.class, args);
        StatClient client = context.getBean(StatClient.class);
        client.saveEndpointHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                "some URI",
                "192.168.0.7",
                LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        client.saveEndpointHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                "some URI",
                "192.168.0.7",
                LocalDateTime.now().minusHours(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        client.saveEndpointHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                "some URI",
                "211.222.0.17",
                LocalDateTime.now().minusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        client.saveEndpointHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                "other URI",
                "211.222.0.17",
                LocalDateTime.now().minusHours(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        client.saveEndpointHit(new EndpointHitDto(
                null,
                "ewm-main-service",
                "one more URI",
                "211.222.0.17",
                LocalDateTime.now().minusHours(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), null, null));
        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), null, false));
        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), null, true));

        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), List.of(), null));
        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), List.of("some URI"), null));
        printStats(client.getStats(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), List.of("other URI", "one more URI"), null));

    }

    private static void printStats(List<ViewStatsDto> stats) {
        System.out.println("---START OF LIST---");
        for (ViewStatsDto stat : stats) {
            System.out.println(stat.getApp());
            System.out.println(stat.getUri());
            System.out.println(stat.getHits());
        }
        System.out.println("----END OF LIST----");
    }
}