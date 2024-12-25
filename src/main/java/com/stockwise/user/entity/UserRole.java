package com.stockwise.user.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.stockwise.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_roles")
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends BaseEntity{
    
    private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "role_uuid", referencedColumnName = "uuid", nullable = false)
	private Role role;

	@ManyToOne
	@JoinColumn(name = "user_uuid", referencedColumnName = "uuid", nullable = false)
	private User user;
}
