package de.samples.quarkus.domain;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class BirthdayStatisticsService {

  private final CustomersClient customersClient;

  public Collection<MonthlyBirthdayStatistic> getMonthlyStatistics() {
    final var countsByMonth = customersClient
      .findAll()
      .collect(Collectors.groupingBy(
        c -> c.getBirthdate().getMonth(),
        Collectors.counting()
      ));

    return Arrays.stream(Month.values())
      .map(month -> {
        var stat = new MonthlyBirthdayStatistic();
        stat.setMonth(month);
        stat.setCount(countsByMonth.getOrDefault(month, 0L));
        return stat;
      })
      .toList();
  }

}
