package org.myfintech.payment.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.projection.ContractWithClientProjection;

public interface ContractService {
	List<ContractDTO> findAll();

	ContractDTO findById(Long id);

	ContractDTO save(ContractCreateDTO dto);

	ContractDTO update(Long id, ContractDTO dto);

	Optional<Contract> findByContractNumber(String contractNumber);

	List<ContractWithClientProjection> findAllByContractNumbers(Set<String> contractNumbers);

	void validateUpdateRequest(Long id, ContractDTO contractDTO);

	void validateCreate(ContractCreateDTO contractDTO);
}
