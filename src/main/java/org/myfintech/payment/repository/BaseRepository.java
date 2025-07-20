package org.myfintech.payment.repository;

import org.myfintech.payment.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends AbstractEntity<ID>, ID> extends JpaRepository<T, ID> {
    // Define shared custom methods if needed
}