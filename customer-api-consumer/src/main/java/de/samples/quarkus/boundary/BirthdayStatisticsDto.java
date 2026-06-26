package de.samples.quarkus.boundary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BirthdayStatisticsDto {

  private String month;
  private long count;

}
