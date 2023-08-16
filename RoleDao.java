package com.ecommerce.backend.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.backend.entity.Role;

@Repository
public interface RoleDao extends CrudRepository<Role, String> {

}