package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.HttpHelper.assertResponseStatusCode;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsHouse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenHouseTest{

	private static final String HOUSE_URL = BASE_URL+"house/";
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void as_a_connected_user_i_can_retrieve_a_house_with_its_given_id(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE_UserWithTokenHouseTest_1\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail (postHouseResponse.getStatusCode()+":"+postHouseResponse.asString());
		}

		// Id
		String houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token
		String token = postHouseResponse.header(AUTH_KEY_HEADER);

		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getHouseResponse = HttpHelper.getResource(HOUSE_URL+houseId, headers);
		if (getHouseResponse.getStatusCode()!=200){
			fail(getHouseResponse.getStatusCode()+" : "+ getHouseResponse.asString());
		}

		// house ?
		assertJsonIsHouse(new JsonPath(getHouseResponse.asString()));
	}

	@Test
	public void as_a_connected_user_i_can_delete_my_house(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE_UserWithTokenHouseTest_2\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE_URL, newHouse);
		if (postHouseResponse.getStatusCode()!=200){
			fail (postHouseResponse.getStatusLine());
		}

		// Id
		String houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token
		String token = postHouseResponse.header(AUTH_KEY_HEADER);

		// Remove house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		HttpHelper.deleteResource(HOUSE_URL+houseId, headers);

		// Retrieve house
		assertResponseStatusCode(404, HttpHelper.getResource(HOUSE_URL+houseId));
		createdHouseIds.remove(houseId);
	}

	public void as_an_occupant_i_can_share_one_of_my_houses_to_another_person_by_mail(){
		fail("tbi");
	}


	

	public void as_an_nonhabitant_i_can_not_visualize_any_info_from_the_house(){
		fail("tbi");
	}

	public void as_an_nonhabitant_i_can_not_add_any_task_to_the_house(){
		fail("tbi");
	}

	@After
	public void cleanHouses(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE_URL+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}