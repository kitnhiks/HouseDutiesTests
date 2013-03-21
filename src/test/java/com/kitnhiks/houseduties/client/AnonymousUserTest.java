package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class AnonymousUserTest{

	private static final String HOUSE_URL = BASE_URL+"house";
	private ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void as_a_anonymous_user_i_can_create_a_new_house_and_retrieve_its_id_and_a_token(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);

		// Id ?
		String id = new JsonPath(postHouseResponse.asString()).getString("id");
		assertNotNull(id);
		createdHouseIds.add(id);

		// Token ?
		assertNotNull(postHouseResponse.header(AUTH_KEY_HEADER));
	}
	
	@Test
	public void as_an_anonymous_user_i_can_log_in_and_retrieve_my_house_id_and_a_token(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
				
		createdHouseIds.add(new JsonPath(postHouseResponse.asString()).getString("id"));
		
		// Login to an existing house
		Response getHouseResponse = HttpHelper.postResourceJson(HOUSE_URL+"/login", newHouse);
		
		int nbTry = 0;
		int maxTry = 5;
		while (nbTry <= maxTry && getHouseResponse.getStatusCode()!=200){
			getHouseResponse = HttpHelper.postResourceJson(HOUSE_URL+"/login", newHouse);
			try{
				Thread.sleep(2000);
			}catch(Exception e){}
			nbTry++;
		}
		if (nbTry == maxTry){
			fail(getHouseResponse.getStatusCode()+" : "+ getHouseResponse.asString());
		}
		
		// Id ?
		String id = new JsonPath(getHouseResponse.asString()).getString("id");
		assertNotNull(id);

		// Token ?
		assertNotNull(getHouseResponse.header(AUTH_KEY_HEADER));
	}
	
	@After
	public void cleanData(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+"/"+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}