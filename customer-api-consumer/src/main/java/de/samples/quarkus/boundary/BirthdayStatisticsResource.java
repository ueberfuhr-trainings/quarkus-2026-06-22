package de.samples.quarkus.boundary;

import de.samples.quarkus.domain.BirthdayStatisticsService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Path("/birthdays/statistics")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class BirthdayStatisticsResource {

  private final BirthdayStatisticsService statisticsService;
  private final BirthdayStatisticsDtoMapper mapper;

  @GET
  public Collection<BirthdayStatisticsDto> getStatistics() {
    return statisticsService
      .getMonthlyStatistics()
      .stream()
      .map(mapper::map)
      .toList();
  }

}
