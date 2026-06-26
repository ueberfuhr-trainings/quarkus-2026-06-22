package de.samples.quarkus.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
public class MonthlyBirthdayStatistic {

  private Month month;
  private long count;

}
