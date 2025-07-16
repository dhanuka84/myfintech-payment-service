package org.myfintech.payment.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.entity.Contract;
import org.myfintech.payment.entity.Payment;
import org.myfintech.payment.entity.PaymentTracking;
import org.myfintech.payment.repository.ContractRepository;
import org.myfintech.payment.repository.PaymentTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Mapping(source = "contract.contractNumber", target = "contractNumber")
	@Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "localDateToString")
	public abstract PaymentDTO toDTO(Payment payment);

	@Mapping(target = "id", ignore = true)
	@Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "stringToLocalDate")
	public abstract Payment toEntity(PaymentCreateDTO dto, @Context Contract contract);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = "stringToLocalDate")
	public abstract Payment toEntity(PaymentDTO dto, @Context Contract contract);

	@AfterMapping
	protected void setContract(@MappingTarget Payment payment, @Context Contract contract) {
		payment.setContract(contract);
		payment.setContractId(contract.getId());
	}
	
	@AfterMapping
	protected void setPaymentTracking(@MappingTarget Payment payment, @Context PaymentTracking tracking) {
		payment.setPaymentTracking(tracking);
		payment.setTrackingId(tracking.getId());
	}

	@Named("stringToLocalDate")
	protected LocalDate stringToLocalDate(String date) {
		return LocalDate.parse(date, DATE_FORMATTER);
	}

	@Named("localDateToString")
	protected String localDateToString(LocalDate date) {
		return date.format(DATE_FORMATTER);
	}
}
