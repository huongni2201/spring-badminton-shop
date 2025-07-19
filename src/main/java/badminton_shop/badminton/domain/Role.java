package badminton_shop.badminton.domain;

import badminton_shop.badminton.utils.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private boolean active;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"roles"})
    @JoinTable(name = "permission_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

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
