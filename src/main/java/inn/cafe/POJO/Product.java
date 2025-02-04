package inn.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Product.getAllProducts", query = "SELECT new inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) FROM Product p")

@NamedQuery(name = "Product.updateProductStatus", query = "UPDATE Product p SET p.status = :status WHERE p.id = :id")

@NamedQuery(name = "Product.getByCategory", query = "SELECT new inn.cafe.wrapper.ProductWrapper(p.id, p.name) FROM Product p WHERE p.category.id = :id AND p.status = 'available'")

@NamedQuery(name = "Product.getProductById", query = "SELECT new inn.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) FROM Product p WHERE p.id = :id")

@Entity
@Data
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
public class Product implements Serializable {

    public static final long SerialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

}
