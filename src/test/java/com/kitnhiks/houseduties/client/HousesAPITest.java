package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;

import java.util.HashMap;

import org.junit.Test;

import com.kitnhiks.houseduties.HttpHelper;

public class HousesAPITest{

	static final String HOUSES = BASE_URL+"houses";

	@Test
	public void should_200_GET_houses_with_admin_rights(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(200, HttpHelper.getResource(HOUSES, headers));
	}

	@Test
	public void should_403_GET_houses_with_bad_key(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(403, HttpHelper.getResource(HOUSES, headers));
	}

	@Test
	public void should_403_GET_houses_with_no_key(){
		assertResponseStatusCode(403, HttpHelper.getResource(HOUSES));
	}
}