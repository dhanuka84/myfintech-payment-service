package org.myfintech.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.projection.ContractWithClientProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContractRepository extends BaseRepository<Contract, Long> {
	
	Optional<Contract> findByContractNumber(String contractNumber);

	@Query(value = """
			SELECT c.id, c.contract_number,
			       cl.id as client_id,
			       cl.client_name as client_name
			FROM contract c
			JOIN client cl ON c.client_id = cl.id
			WHERE c.contract_number IN (:contractNumbers)
			""", nativeQuery = true)
	List<ContractWithClientProjection> findAllByContractNumberIn(@Param("contractNumbers") Set<String> contractNumbers);

}
