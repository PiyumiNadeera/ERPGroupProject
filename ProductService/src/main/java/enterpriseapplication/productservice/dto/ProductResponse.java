package enterpriseapplication.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long productId;
    private String name;
    private String description;
    private String category;
    private BigDecimal unitPrice;
    private Integer stock;
}
