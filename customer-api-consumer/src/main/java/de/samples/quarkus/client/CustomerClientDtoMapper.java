package de.samples.quarkus.client;

import de.samples.quarkus.domain.Customer;
import de.samples.quarkus.shared.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface CustomerClientDtoMapper {

  @Mapping(target = "uuid", source = "uuid")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "birthdate", source = "birthdate")
  Customer map(CustomerClientDto source);

}
