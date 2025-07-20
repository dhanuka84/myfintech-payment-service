package org.myfintech.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.mapper.ClientMapper;
import org.myfintech.payment.repository.ClientRepository;
import org.myfintech.payment.service.impl.ClientServiceImpl;
import org.myfintech.payment.validator.ClientValidator;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private ClientValidator restValidator;

    @Spy
    private ClientMapper clientMapper =  Mappers.getMapper( ClientMapper.class );

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;
    private ClientCreateDTO clientCreateDTO;

    @BeforeEach
    void setUp() {
    	OffsetDateTime now = OffsetDateTime.now();
        client = new Client(1L,now,now, "Acme");
        clientDTO = new ClientDTO(1L, "Acme");
        clientCreateDTO = new ClientCreateDTO("Acme");
    }

    @Test
    void shouldReturnAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        List<ClientDTO> result = clientService.findAll();
        assertEquals(1, result.size());
        assertEquals("Acme", result.get(0).clientName());
    }

    @Test
    void shouldReturnClientById() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        ClientDTO result = clientService.findById(1L);
        assertEquals("Acme", result.clientName());
    }

    @Test
    void shouldSaveClient() {
        when(clientRepository.save(any())).thenReturn(client);
        ClientDTO result = clientService.save(clientCreateDTO);
        assertEquals("Acme", result.clientName());
    }

    @Test
    void shouldUpdateClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        ClientDTO result = clientService.update(1L, clientDTO);
        assertEquals("Acme", result.clientName());
    }
}
