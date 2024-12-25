package com.stockwise.user.dao;

import com.stockwise.user.entity.Role;
import com.stockwise.user.enums.UserType;

public interface RoleDao {
    
    Role roleByUserType(UserType userType);
}
