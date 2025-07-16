
package org.myfintech.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {
	@Mapping(source = "id", target = "clientId")
    ClientDTO toDTO(Client client);
	
    Client toEntity(ClientCreateDTO dto);
}
