package org.myfintech.payment.service.impl;

import java.util.List;

import org.myfintech.payment.domain.ClientCreateDTO;

/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */

import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.mapper.ClientMapper;
import org.myfintech.payment.repository.ClientRepository;
import org.myfintech.payment.service.ClientService;
import org.myfintech.payment.validator.ClientValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final ClientValidator validator;

    public ClientServiceImpl(ClientRepository repository, ClientMapper mapper, ClientValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    public List<ClientDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public ClientDTO findById(Long id) {
        return repository.findById(id).map(mapper::toDTO).orElseThrow(() -> new IllegalArgumentException("client not found: " + id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ClientDTO save(ClientCreateDTO dto) {
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ClientDTO update(Long id, ClientDTO dto) {
    	validate(dto);
        Client client = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("client not found: " + id));
        client.setClientName(dto.clientName());
        return mapper.toDTO(client);
    }

	@Override
	public Client findEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("client not found: " + id));
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void validate(ClientDTO dto) {
		validator.validateClientNameRequest(dto);
		
	}
}
