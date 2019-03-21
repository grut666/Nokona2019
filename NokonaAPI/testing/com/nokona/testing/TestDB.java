package com.nokona.testing;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.nokona.data.NokonaDatabaseEmp;

class TestDB {
	@Inject
	private  NokonaDatabaseEmp db;
	@Test
	void test() {
		assertTrue("Null db", db != null);
	}

}
