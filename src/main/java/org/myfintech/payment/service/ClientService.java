package org.myfintech.payment.service;

import java.util.List;

import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.entity.Client;

public interface ClientService {
    List<ClientDTO> findAll();
    ClientDTO findById(Long id);
    Client findEntityById(Long id);
    ClientDTO save(ClientCreateDTO dto);
    ClientDTO update(Long id, ClientDTO dto);
    void validate(ClientDTO dto);
}
