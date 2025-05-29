package com.galaxyviewtower.hotel.crud.model;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("hotels") // Maps to the H2 table (plural)
public class Hotel {
  @Id private String id;
  private String name;
  private String address; // was city
  private BigDecimal rating; // was pricePerNight
}
