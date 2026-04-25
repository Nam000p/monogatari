package com.monogatari.app.entity;

import com.monogatari.app.enums.AuthProvider;
import com.monogatari.app.enums.SystemRole;
import com.monogatari.app.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 255)
	private String username;
	
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "birth_date")
    private LocalDate birthDate;
	
	@Column(nullable = false, length = 255)
	private String password;
	
	@Column(name = "avatar_url", columnDefinition = "TEXT")
	private String avatarUrl;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private SystemRole role = SystemRole.ROLE_USER;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private UserStatus status = UserStatus.UNVERIFIED;

	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider", nullable = false, length = 20)
	@Builder.Default
	private AuthProvider authProvider = AuthProvider.LOCAL;

	@Column(name = "stripe_customer_id", unique = true, length = 100)
	private String stripeCustomerId;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Instant createdAt;

	@Transient
    public Integer calculateAge() {
        if (this.birthDate == null) {
            return 0;
        }
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
}