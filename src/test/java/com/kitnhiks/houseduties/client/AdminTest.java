package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsHouse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class AdminTest{
	private static final String HOUSE = BASE_URL+"house";
	private static final String HOUSES = BASE_URL+"houses";
	private ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void as_an_admin_i_can_retrieve_informations_of_a_created_houses(){
		// Create House 1
		String newHouse1Name = "TEST_HOUSE_1";
		String newHouse1Pass = "TEST_HOUSE_PWD_1";
		String newHouse1 = "{\"name\":\""+newHouse1Name+"\", \"password\":\""+newHouse1Pass+"\"}";
		String newHouse1Id = new JsonPath (HttpHelper.postResourceJson(HOUSE, newHouse1).asString()).getString("id");
		createdHouseIds.add(newHouse1Id);

		// Retrieve House
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		Response response = HttpHelper.getResource(HOUSE+"/"+newHouse1Id, headers);
		if (response.getStatusCode()!=200){
			fail(response.getStatusCode()+" : "+ response.asString());
		}
		JsonPath house1Json = new JsonPath (response.asString());

		// Created Houses ?
		Map<String, String> house1 = house1Json.get("");
		assertJsonIsHouse(house1);
		assertEquals(newHouse1Name, house1.get("name"));
	}

	@Test
	public void as_an_admin_i_can_delete_created_houses(){
		// Create House 1
		String newHouse1Name = "TEST_HOUSE_2";
		String newHouse1Pass = "TEST_HOUSE_PWD_2";
		String newHouse1 = "{\"name\":\""+newHouse1Name+"\", \"password\":\""+newHouse1Pass+"\"}";
		Response response = HttpHelper.postResourceJson(HOUSE, newHouse1);
		if (response.getStatusCode()!=200){
			fail (response.getStatusCode() +":"+ response.getStatusLine());
		}
		String newHouse1Id = new JsonPath (response.asString()).getString("id");
		createdHouseIds.add(newHouse1Id);

		// Delete House
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		HttpHelper.deleteResource(HOUSE+"/"+newHouse1Id, headers);

		// Retrieve House
		assertResponseStatusCode(404, HttpHelper.getResource(HOUSE+"/"+newHouse1Id, headers));
		createdHouseIds.remove(newHouse1Id);
	}


	@After
	public void cleanData(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE+"/"+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}