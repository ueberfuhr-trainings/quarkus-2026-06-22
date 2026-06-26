package de.samples.quarkus.boundary;

import de.samples.quarkus.domain.MonthlyBirthdayStatistic;
import de.samples.quarkus.shared.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Mapper(config = MapStructConfig.class)
public interface BirthdayStatisticsDtoMapper {

  @Mapping(target = "month", source = "month")
  BirthdayStatisticsDto map(MonthlyBirthdayStatistic source);

  default String mapMonth(Month month) {
    return month.getDisplayName(TextStyle.FULL, Locale.GERMAN);
  }

}
