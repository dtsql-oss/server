package com.tsdl.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceApplicationTest {

	@Test
	void contextLoads() {
	}

	@Test
	void dummyTest_service() {
		assertThat(5).isEqualTo(5);
	}

}
