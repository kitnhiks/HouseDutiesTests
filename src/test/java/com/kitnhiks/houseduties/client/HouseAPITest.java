package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class HouseAPITest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static final String LOGIN_URL = HOUSE_URL+"login/";
	private ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void should_200_POST_Login_GET_DELETE_house_with_minimum_correct_json(){
		String newHouse = "{\"name\":\"TEST_HOUSE_HouseAPITest\", \"password\":\"TEST_HOUSE_PWD\"}";

		// POST house
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		assertResponseStatusCode(200, postHouseResponse);
		String id = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(id);

		// GET house
		assertResponseStatusCode(200, HttpHelper.getResource(HOUSE_URL+id));

		// Login house
		Response loginHouseResponse = HttpHelper.postResourceJson(LOGIN_URL, newHouse);
		assertResponseStatusCode(200, loginHouseResponse);
		assertEquals(id, new JsonPath(loginHouseResponse.asString()).getString("id"));

		// DELETE house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(200, HttpHelper.deleteResource(HOUSE_URL+id, headers));
	}

	@Test
	public void should_400_POST_house_with_incorrect_json(){
		String newHouse = "{\"badkey\":\"TEST_HOUSE_HouseAPITest\", \"password\":\"TEST_HOUSE_PWD\"}";
		assertResponseStatusCode(400, HttpHelper.postResourceJson(HOUSE_URL, newHouse));
	}

	@Test
	public void should_400_POST_house_with_invalid_json(){
		String newHouse = "name\":\"TEST_HOUSE_HouseAPITest\", \"password\":\"TEST_HOUSE_PWD\"}";
		assertResponseStatusCode(400, HttpHelper.postResourceJson(HOUSE_URL, newHouse));
	}

	@Test
	public void should_401_POST_already_existing_house_name(){
		String newHouse = "{\"name\":\"TEST_HOUSE_HouseAPITest\", \"password\":\"TEST_HOUSE_PWD\"}";
		String newHouse2 = "{\"name\":\"TEST_HOUSE_HouseAPITest\", \"password\":\"TEST_HOUSE_PWD2\"}";

		// POST house
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail (postHouseResponse.getStatusCode() + " : "+ postHouseResponse.getStatusLine());
		}
		createdHouseIds.add(new JsonPath(postHouseResponse.asString()).getString("id"));

		// POST house same name and password
		assertResponseStatusCode(401, HttpHelper.postResourceJson(HOUSE_URL, newHouse));
		// POST house same name and different password
		assertResponseStatusCode(401, HttpHelper.postResourceJson(HOUSE_URL, newHouse2));
	}

	@Test
	public void should_404_loging_unexisting_house(){
		String newHouse = "{\"name\":\"UKNWN_HOUSE\", \"password\":\"UKNWN_HOUSE_PWD\"}";
		assertResponseStatusCode(404, HttpHelper.postResourceJson(LOGIN_URL, newHouse));
	}

	@Test
	public void should_404_GET_house_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.getResource(HOUSE_URL+"1", headers));
	}

	@Test
	public void should_401_DELETE_house(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(404, HttpHelper.getResource(HOUSE_URL+"1", headers));
	}

	@Test
	public void should_404_DELETE_house_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.deleteResource(HOUSE_URL+"1", headers));
	}


	////////// Tools //////////

	@After
	public void cleanData(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}