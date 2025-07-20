package org.myfintech.payment.repository;

import java.util.Optional;

import org.myfintech.payment.entity.PaymentTracking;

public interface PaymentTrackingRepository extends BaseRepository<PaymentTracking, Long> {
	Optional<PaymentTracking> findByTrackingNumber(String trackingNumber);
	
	
}
