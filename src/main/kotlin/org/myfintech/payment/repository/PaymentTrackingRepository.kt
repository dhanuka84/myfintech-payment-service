package org.myfintech.payment.repository

import org.myfintech.payment.entity.PaymentTracking
import java.util.*

interface PaymentTrackingRepository : BaseRepository<PaymentTracking, Long> {
    fun findByTrackingNumber(trackingNumber: String?): Optional<PaymentTracking>
}
