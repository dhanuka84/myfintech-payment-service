package org.myfintech.payment.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.entity.Client;
import org.myfintech.payment.entity.Contract;

@Mapper(componentModel = "spring")
public abstract class ContractMapper {

	@Mapping(source = "client.id", target = "clientId")
	@Mapping(source = "id", target = "contractId")
    public abstract ContractDTO toDTO(Contract contract);
    
    public abstract Contract toEntity(ContractCreateDTO dto, @Context Client client);

    @AfterMapping
	protected void setClient(@MappingTarget Contract contract, @Context Client client) {
    	contract.setClient(client);

	}
}
