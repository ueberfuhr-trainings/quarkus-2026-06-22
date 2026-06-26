package de.samples.quarkus.shared.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
  componentModel = "cdi",
  unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MapStructConfig {
}
