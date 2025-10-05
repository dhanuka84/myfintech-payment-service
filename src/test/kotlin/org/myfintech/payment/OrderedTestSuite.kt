package org.myfintech.payment

import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import org.myfintech.payment.api.v1.ClientControllerTest
import org.myfintech.payment.integration.ClientControllerIntegrationTest
import org.myfintech.payment.integration.testcontainers.ClientControllerRestAssuredTest
import org.myfintech.payment.integration.testcontainers.ClientServiceIntegrationTest
import org.myfintech.payment.service.ClientServiceImplTest

@Suite
@SelectClasses(
    ClientControllerTest::class,
    ClientServiceImplTest::class,
    ClientControllerIntegrationTest::class,
    ClientServiceIntegrationTest::class,
    ClientControllerRestAssuredTest::class
)
class OrderedTestSuite 
