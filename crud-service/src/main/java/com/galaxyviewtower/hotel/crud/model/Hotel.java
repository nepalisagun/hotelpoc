package com.galaxyviewtower.hotel.crud.model;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("hotel") // Maps to the H2 table
public class Hotel {
  @Id private String id;
  private String name;
  private String city;
  private BigDecimal pricePerNight;
}
