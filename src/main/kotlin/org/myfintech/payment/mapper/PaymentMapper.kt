package org.myfintech.payment.mapper

import org.mapstruct.Context
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.Payment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Mapper(componentModel = "spring")
interface PaymentMapper {

    @Mapping(source = "contract.contractNumber", target = "contractNumber")
    @Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = ["localDateToString"])
    fun toDTO(payment: Payment): PaymentDTO

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "contract", source = "contract")
    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "paymentTracking", ignore = true)
    @Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = ["stringToLocalDate"])
    fun toEntity(dto: PaymentCreateDTO, @Context contract: Contract): Payment

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "contract", source = "contract")
    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "paymentTracking", ignore = true)
    @Mapping(target = "trackingId", ignore = true)
    @Mapping(source = "paymentDate", target = "paymentDate", qualifiedByName = ["stringToLocalDate"])
    fun toEntity(dto: PaymentDTO, @Context contract: Contract): Payment

    @Named("stringToLocalDate")
    fun stringToLocalDate(date: String): LocalDate {
        return LocalDate.parse(date, DATE_FORMATTER)
    }

    @Named("localDateToString")
    fun localDateToString(date: LocalDate): String {
        return date.format(DATE_FORMATTER)
    }
}