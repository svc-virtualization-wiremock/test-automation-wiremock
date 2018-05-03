package com.sudhirt.api.rest.repository;

import com.sudhirt.api.rest.entity.Address;
import com.sudhirt.api.rest.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    List<Address> findByCustomerId(String customerId);

    Optional<Address> getByIdAndCustomerId(String id, String customerId);
}
