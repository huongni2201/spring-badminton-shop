package badminton_shop.badminton.domain;

import java.time.Instant;
import java.util.List;

import badminton_shop.badminton.utils.SecurityUtil;
import badminton_shop.badminton.utils.constant.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import badminton_shop.badminton.utils.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String fullName;
    private String phone;
    private String email;

    private String address;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "qr_code_url", length = 1024)
    private String qrCodeUrl;


    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.updatedAt = Instant.now();
    }
}
