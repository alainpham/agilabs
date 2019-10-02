package io.openshift.booster;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.openshift.booster.Customer;

@RepositoryRestResource
public interface CustomerRepo extends PagingAndSortingRepository<Customer, String> {

    public Page<Customer> findByName(@Param(value = "name") String name, Pageable p);
    public Page<Customer> findByAge(@Param(value = "age") Integer age, Pageable p);
	
}