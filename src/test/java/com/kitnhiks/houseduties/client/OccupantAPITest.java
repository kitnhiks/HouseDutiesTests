package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class OccupantAPITest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static final String LOGIN_URL = HOUSE_URL+"login/";
	private static String OCCUPANT_URL;
	private static String houseId;
	private static String token;
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();
	private ArrayList<String> createdOccupantsIds = new ArrayList<String>();

	
	
	@Test
	public void should_200_POST_GET_DELETE_occupant_with_minimum_correct_json(){
		String newOccupant = "{\"name\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";

		// POST occupant
		Response postOccupantResponse = HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant);
		assertResponseStatusCode(200, postOccupantResponse);
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);
		
		// GET occupant
		assertResponseStatusCode(200, HttpHelper.getResource(OCCUPANT_URL+id));
		
		// DELETE occupant
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(200, HttpHelper.deleteResource(OCCUPANT_URL+id, headers));
	}

	@Test
	public void should_400_POST_occupant_with_incorrect_json(){
		String newOccupant = "{\"badkey\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(400, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant));
	}

	@Test
	public void should_400_POST_occupant_with_invalid_json(){
		String newOccupant = "name\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		assertResponseStatusCode(400, HttpHelper.postResourceJson(OCCUPANT_URL, newOccupant));
	}
	
	@Test
	public void should_404_GET_occupant_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.getResource(OCCUPANT_URL+"0", headers));
	}

	@Test
	public void should_401_DELETE_occupant(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, "BAD_KEY");
		assertResponseStatusCode(404, HttpHelper.getResource(OCCUPANT_URL+"0", headers));
	}

	@Test
	public void should_404_DELETE_occupant_unexisting(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		assertResponseStatusCode(404, HttpHelper.deleteResource(OCCUPANT_URL+"0", headers));
	}

	////////// Tools //////////

	@BeforeClass
	public static void createHouse(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		// Id
		houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token 
		token = postHouseResponse.header(AUTH_KEY_HEADER);
		// Url 
		OCCUPANT_URL = HOUSE_URL+houseId+"/occupant/";
	}
	
	
	@After
	public void cleanOccupants(){
		// Remove created occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdOccupantsIds){
			HttpHelper.deleteResource(OCCUPANT_URL+id, headers);
		}
		createdOccupantsIds = new ArrayList<String>();
	}

	@AfterClass
	public static void cleanHouses(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}