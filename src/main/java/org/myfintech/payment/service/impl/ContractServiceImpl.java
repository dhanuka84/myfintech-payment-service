package org.myfintech.payment.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.projection.ContractWithClientProjection;
import org.myfintech.payment.exception.Http404NotFoundException;
import org.myfintech.payment.mapper.ContractMapper;
import org.myfintech.payment.repository.ContractRepository;
import org.myfintech.payment.service.ClientService;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.validator.CotractValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.CONTRACT_NOT_FOUND;

@Service
public class ContractServiceImpl implements ContractService {

	private final ContractRepository repository;
	private final ContractMapper mapper;
	private final ClientService clientService;
	private final CotractValidator validator;

	public ContractServiceImpl(ContractRepository repository, ContractMapper mapper, ClientService clientService,
			CotractValidator validator) {
		this.repository = repository;
		this.mapper = mapper;
		this.clientService = clientService;
		this.validator = validator;
	}

	@Override
	public List<ContractDTO> findAll() {
		return repository.findAll().stream().map(mapper::toDTO).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ContractDTO findById(Long id) {
		return repository.findById(id).map(mapper::toDTO).orElseThrow();
	}

	@Override
	@Transactional
	public ContractDTO save(ContractCreateDTO dto) {
		return mapper.toDTO(repository.save(mapper.toEntity(dto, clientService.findEntityById(dto.clientId()))));
	}

	@Override
	@Transactional
	public ContractDTO update(Long id, ContractDTO dto) {
		Contract contract = repository.findById(id).orElseThrow(() -> new Http404NotFoundException(CONTRACT_NOT_FOUND + id));
		contract.setContractNumber(dto.contractNumber());
		return mapper.toDTO(contract);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Contract> findByContractNumber(String contractNumber) {
		return Optional.of(repository.findByContractNumber(contractNumber)
				.orElseThrow(() -> new Http404NotFoundException(CONTRACT_NOT_FOUND + contractNumber)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ContractWithClientProjection> findAllByContractNumbers(Set<String> contractNumbers) {
		return repository.findAllByContractNumberIn(contractNumbers);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void validateUpdateRequest(Long id, ContractDTO contractDTO) {
		validator.validateUpdateRequest(id, contractDTO);
		
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void validateCreate(ContractCreateDTO contractDTO) {
		validator.validateCreate(contractDTO);
		
	}
}
