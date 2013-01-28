package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class HouseAPITest{

	private static final String HOUSE = BASE_URL+"house";
	private ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void should_200_POST_GET_DELETE_house_with_minimum_correct_json(){
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";

		// POST house
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE, newHouse);
		assertResponseStatusCode(200, postHouseResponse);

		// GET house
		String id = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(id);
		assertResponseStatusCode(200, HttpHelper.getResource(HOUSE+"/"+id));

		// DELETE house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(200, HttpHelper.deleteResource(HOUSE+"/"+id, headers));
	}

	@Test
	public void should_400_POST_house_with_incorrect_json(){
		String newHouse = "{\"badkey\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response response = HttpHelper.postResourceJson(HOUSE, newHouse);
		assertResponseStatusCode(400, response);
	}

	@Test
	public void should_400_POST_house_with_invalid_json(){
		String newHouse = "name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response response = HttpHelper.postResourceJson(HOUSE, newHouse);
		assertResponseStatusCode(400, response);
	}

	@Test
	public void should_404_GET_house_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.getResource(HOUSE+"/0", headers));
	}

	@Test
	public void should_401_DELETE_house(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(404, HttpHelper.getResource(BASE_URL+"house/0", headers));
	}

	@Test
	public void should_404_DELETE_house_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.deleteResource(BASE_URL+"house/0", headers));
	}

	////////// Tools //////////
	
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