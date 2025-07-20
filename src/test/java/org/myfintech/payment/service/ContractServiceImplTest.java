package org.myfintech.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.mapper.ContractMapper;
import org.myfintech.payment.repository.ClientRepository;
import org.myfintech.payment.repository.ContractRepository;
import org.myfintech.payment.service.impl.ContractServiceImpl;
import org.myfintech.payment.validator.CotractValidator;

@ExtendWith(MockitoExtension.class)
public class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;
    
    @Mock
    private ClientService clientService;

    @Mock
    private CotractValidator validator;

    @InjectMocks
    private ContractServiceImpl contractService;
    
    @Spy
    private ContractMapper contractMapper =  Mappers.getMapper( ContractMapper.class );

    private Contract contract;
    private ContractDTO contractDTO;
    private ContractCreateDTO contractCreateDTO;
    private Client client;

    @BeforeEach
    void setUp() {
    
    	contractService = spy( new ContractServiceImpl( contractRepository, contractMapper,clientService,validator ) );
    	OffsetDateTime now = OffsetDateTime.now();
        client = new Client(1L,now,now, "Acme");
        contract = new Contract(1L,now,now, client, "12345");
        contractDTO = new ContractDTO(1L, 1L, "12345");
        contractCreateDTO = new ContractCreateDTO(1L, "12345");
    }

    @Test
    void shouldReturnAllContracts() {
        when(contractRepository.findAll()).thenReturn(List.of(contract));
        List<ContractDTO> result = contractService.findAll();
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).contractNumber());
    }

    @Test
    void shouldReturnContractById() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        ContractDTO result = contractService.findById(1L);
        assertEquals("12345", result.contractNumber());
    }

    @Test
    void shouldSaveContract() {
        when(clientService.findEntityById(1L)).thenReturn(client);
        when(contractRepository.save(any())).thenReturn(contract);
        ContractDTO result = contractService.save(contractCreateDTO);
        assertEquals("12345", result.contractNumber());
    }

    @Test
    void shouldUpdateContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        ContractDTO result = contractService.update(1L, contractDTO);
        assertEquals("12345", result.contractNumber());
    }
}
