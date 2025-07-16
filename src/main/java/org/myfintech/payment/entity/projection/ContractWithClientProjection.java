/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.entity.projection;

public interface ContractWithClientProjection {

    // Fields from Contract table
    Long getId();

    String getContractNumber();

    // Fields from Client table (aliased with cl_*)
    Long getClientId();

    String getClientName();
}
