package com.stockwise.user.entity;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.DynamicUpdate;

import com.stockwise.common.emuns.EntityStatus;
import com.stockwise.common.entity.BaseEntity;
import com.stockwise.user.enums.Gender;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity{
    
    private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EntityStatus entityStatus = EntityStatus.ACTIVE;

    @Column(nullable = false, unique = true)
	private String uuid;

	@Column
	private String firstName;

	@Column
	private String lastName;

	@Column
	@Enumerated(EnumType.STRING)
	private Gender gender;

    @Email
	private String email;

	@Column
	private Boolean isEmailVerified = false;

	@Column
	private String phoneNumber;

	@Column
	private Boolean isPhoneVerified = false;

	@Column
	private String phoneCountryCode;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserRole> userRoles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserIdentity> userIdentities;

	public String getCommaSeparatedRoles() {
		if (userRoles == null || userRoles.isEmpty()) {
			return "";
		}

		return userRoles.stream().map(userRole -> userRole.getRole().getUserType().name())
				.collect(Collectors.joining(", "));
	}
}
