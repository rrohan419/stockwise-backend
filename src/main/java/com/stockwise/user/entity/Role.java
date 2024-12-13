package com.stockwise.user.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.stockwise.common.entity.BaseEntity;
import com.stockwise.user.enums.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Role extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String uuid;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private UserType userType;
}
