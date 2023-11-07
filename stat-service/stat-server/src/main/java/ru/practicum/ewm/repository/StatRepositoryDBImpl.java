package ru.practicum.ewm.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.exception.NoStatsForSuchApplicationException;
import ru.practicum.ewm.model.EndpointHit;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StatRepositoryDBImpl implements StatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ViewStatsDto saveHit(EndpointHit hit) {
        int appId = getAppId(hit.getApp());

        String insertSql = "insert into endpoints_hits (app_id, uri, ip_address, visited) values (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"endpoint_hit_id"});
            ps.setInt(1, appId);
            ps.setString(2, hit.getUri());
            ps.setLong(3, hit.getIp());
            ps.setTimestamp(4, Timestamp.valueOf(hit.getTimestamp()));
            return ps;
        }, keyHolder);

        log.info("New hit with id={}, app={}, uri={}, ip={} and visited={} has been saved",
                keyHolder.getKey().longValue(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());

        String selectSql = "select count(ip_address) as hits " +
                "from endpoints_hits " +
                "where app_id = ? " +
                "and uri = ?";

        return jdbcTemplate.query(selectSql,
                (resultSet, rowNumber) -> new ViewStatsDto(hit.getApp(), hit.getUri(), resultSet.getLong("hits")),
                appId, hit.getUri())
                .stream().findAny().get();
    }

    @Override
    public List<ViewStatsDto> getStats(List<LocalDateTime> period, List<String> uris, boolean unique, String app) {
        int appId = getAppId(app);

        String inSql = "";

        if (uris != null && !uris.isEmpty()) {
            StringBuilder sb = new StringBuilder("(");
            for (String uri : uris) {
                sb.append("?,");
            }
            sb.deleteCharAt(sb.length() - 1).append(") ");
            inSql = sb.toString();
        }

        String sql = "select uri, " + (unique ? "count(distinct ip_address) as hits " : "count(ip_address) as hits ") +
                "from endpoints_hits " +
                "where visited between ? and ? " +
                "and app_id = ? " +
                (uris != null && !uris.isEmpty() ? "and uri in " + inSql : "") +
                "group by uri " +
                "order by hits desc";

        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(period.get(0)));
            ps.setTimestamp(2, Timestamp.valueOf(period.get(1)));
            ps.setInt(3, appId);
            if (uris != null && !uris.isEmpty()) {
                int i = 4;
                for (String uri : uris) {
                    ps.setString(i++, uri);
                }
            }
            return ps;
        }, (resultSet, rowNumber) -> new ViewStatsDto(app, resultSet.getString("uri"), resultSet.getLong("hits")));
    }

    private int getAppId(String app) {
        Optional<Integer> appId = jdbcTemplate.query("select app_id from applications where app_name = ?",
                        (resultSet, rowNumber) -> resultSet.getInt("app_id"), app)
                .stream()
                .findAny();
        if (appId.isEmpty()) {
            throw new NoStatsForSuchApplicationException("There is no stats data for application " + app);
        }
        return appId.get();
    }
}