package org.myfintech.payment;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.myfintech.payment.api.v1.ClientControllerTest;
import org.myfintech.payment.api.v1.ContractControllerTest;
import org.myfintech.payment.api.v1.PaymentControllerTest;
import org.myfintech.payment.integration.ClientControllerIntegrationTest;
import org.myfintech.payment.integration.PaymentServiceFacadeImplIntegrationTest;
import org.myfintech.payment.integration.testcontainers.ClientServiceIntegrationTest;
import org.myfintech.payment.service.ClientServiceImplTest;
import org.myfintech.payment.service.ContractServiceImplTest;
import org.myfintech.payment.service.PaymentServiceImplTest;

@Suite
@SelectClasses({
	PaymentControllerTest.class,
	ContractControllerTest.class,
	ClientControllerTest.class,
	PaymentServiceImplTest.class,
	ContractServiceImplTest.class,
	ClientServiceImplTest.class,
	ClientControllerIntegrationTest.class,
	PaymentServiceFacadeImplIntegrationTest.class,
	ClientServiceIntegrationTest.class
})
public class OrderedTestSuite {

}
