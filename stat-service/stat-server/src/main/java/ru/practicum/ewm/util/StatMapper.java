package ru.practicum.ewm.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.exception.EndBeforeStartException;
import ru.practicum.ewm.exception.InvalidTimeFormatException;
import ru.practicum.ewm.exception.NotIpAddressException;
import ru.practicum.ewm.exception.TimestampInFutureException;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@UtilityClass
public class StatMapper {
    public EndpointHit mapToEndpointHit(EndpointHitDto hitDto) {
        return new EndpointHit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                mapIpToLong(hitDto.getIp()),
                mapTimestampToPastLocalDateTime(hitDto.getTimestamp(), "Timestamp")
        );
    }

    public List<LocalDateTime> mapPeriod(String start, String end) {
        LocalDateTime mappedStart = mapTimestampToPastLocalDateTime(start, "Start");
        LocalDateTime mappedEnd = mapTimestampToLocalDateTime(end, "End");
        if (mappedEnd.isBefore(mappedStart)) {
            throw new EndBeforeStartException("Start should be before end. But received start = " + start +
                    ", end = " + end);
        }
        return List.of(mappedStart, mappedEnd);
    }

    private long mapIpToLong(String ip) {
        long result = 0L;
        String[] numbers = ip.split("\\.");
        if (numbers.length != 4) {
            throw new NotIpAddressException("Ip expected to be of pattern k.l.m.n, where k, l, m, n - integers " +
                    "from 0 to 255. But received: " + ip);
        }
        for (int i = 0; i < numbers.length; i++) {
            try {
                long number = Long.parseLong(numbers[i]);
                if (number < 0 || number > 255) {
                    throw new NotIpAddressException("");
                }
                result += (long) Math.pow(1_000L, (numbers.length - i - 1)) * number;
            } catch (Throwable exception) {
                throw new NotIpAddressException("Ip expected to be of pattern k.l.m.n, where k, l, m, n - integers " +
                        "from 0 to 255. But received: " + ip);
            }
        }
        return result;
    }

    private LocalDateTime mapTimestampToPastLocalDateTime(String timestamp, String fieldName) {
        try {
            LocalDateTime result = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (result.isAfter(LocalDateTime.now())) {
                throw new TimestampInFutureException(fieldName + " should not be in future. But received: " + timestamp);
            }
            return result;
        } catch (DateTimeParseException exception) {
            throw new InvalidTimeFormatException(fieldName + " expected to be of pattern yyyy-MM-dd HH:mm:ss. But received: " +
                    timestamp);
        }
    }

    private LocalDateTime mapTimestampToLocalDateTime(String timestamp, String fieldName) {
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException exception) {
            throw new InvalidTimeFormatException(fieldName + " expected to be of pattern yyyy-MM-dd HH:mm:ss. But received: " +
                    timestamp);
        }
    }
}