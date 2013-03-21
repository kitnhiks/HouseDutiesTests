package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsHouse;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsOccupant;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenTest{

	private static final String HOUSE = BASE_URL+"house/";
	private static ArrayList<String> createdHouseIds = new ArrayList<String>();
	private static ArrayList<String> createdOccupantsIds = new ArrayList<String>();

	private static String houseId;
	private static String token;

	@BeforeClass
	public static void createHouse(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE, newHouse);
		// Id
		houseId = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(houseId);
		// Token 
		token = postHouseResponse.header(AUTH_KEY_HEADER);
	}


	@Test
	public void as_a_user_i_can_retrieve_a_house_with_its_given_id(){
		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getHouseResponse = HttpHelper.getResource(HOUSE+houseId, headers);
		if (getHouseResponse.getStatusCode()!=200){
			fail(getHouseResponse.getStatusCode()+" : "+ getHouseResponse.asString());
		}

		// house ?
		assertJsonIsHouse(new JsonPath(getHouseResponse.asString()));

		// Token 
		token = getHouseResponse.header(AUTH_KEY_HEADER);
	}

	@Test
	public void as_a_connected_user_i_can_add_an_occupant_to_my_house(){
		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusCode()+" : "+ postOccupantResponse.asString());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail("Impossible de récupérer les occupants de la maison "+houseId+ "("+getOccupantsResponse.getStatusCode()+" : "+ getOccupantsResponse.asString()+")");
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(1, occupantsList.size());
		Map <String, String> occupant =occupantsList.get(0);
		assertJsonIsOccupant(occupant);
		assertEquals("TEST_OCCUPANT", occupant.get("name"));
		assertEquals(0, occupant.get("points"));

		// Token 
		token = getOccupantsResponse.header(AUTH_KEY_HEADER);
	}

	@Test
	public void as_a_connected_user_i_can_add_an_occupant_without_password_to_my_house(){
		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusCode()+" : "+ postOccupantResponse.asString());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response getOccupantsResponse = HttpHelper.getResource(HOUSE+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail("Impossible de récupérer les occupants de la maison "+houseId+ "("+getOccupantsResponse.getStatusCode()+" : "+ getOccupantsResponse.asString()+")");
		}

		List<Map<String, String>> occupantsList = new JsonPath(getOccupantsResponse.asString()).getList("");

		assertEquals(1, occupantsList.size());
		Map <String, String> occupant =occupantsList.get(0); 
		assertJsonIsOccupant(occupant);
		assertEquals("TEST_OCCUPANT", occupant.get("name"));
		assertEquals(0, occupant.get("points"));

		// Token 
		token = getOccupantsResponse.header(AUTH_KEY_HEADER);
	}

	public void as_a_connected_user_i_can_remove_an_occupant_from_my_house(){
		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		String newOccupant = "{\"name\":\"TEST_OCCUPANT\", \"password\":\"TEST_OCCUPANT_PWD\"}";
		Response postOccupantResponse = HttpHelper.postResourceJson(HOUSE+houseId+"/occupant", newOccupant, headers);

		if (postOccupantResponse.getStatusCode()!=200){
			fail(postOccupantResponse.getStatusCode()+" : "+ postOccupantResponse.asString());
		}

		// Id ?
		String id = new JsonPath(postOccupantResponse.asString()).getString("id");
		createdOccupantsIds.add(id);

		Response deleteOccupantsResponse = HttpHelper.deleteResource(HOUSE+houseId+"/occupant/"+id, headers);


		Response getOccupantsResponse = HttpHelper.getResource(HOUSE+houseId+"/occupants", headers);

		if (getOccupantsResponse.getStatusCode()!=200){
			fail("Impossible de récupérer les occupants de la maison "+houseId+ "("+getOccupantsResponse.getStatusCode()+" : "+ getOccupantsResponse.asString()+")");
		}

		createdOccupantsIds.remove(id);

		List<Map<String, String>> occupantsList = new JsonPath(deleteOccupantsResponse.asString()).getList("");

		assertEquals(0, occupantsList.size());

		// Token 
		token = getOccupantsResponse.header(AUTH_KEY_HEADER);
	}

	public void as_an_connected_user_i_can_delete_a_house(){
		fail("tbi");
	}
	
	public void as_a_connected_user_i_can_list_all_occupants_from_my_house(){
		fail("tbi");
	}
	
	public void as_a_connected_user_i_cannot_add_an_occupant_to_another_house(){
		fail("tbi");
	}
	
	public void as_a_connected_user_i_cannot_remove_an_occupant_from_another_house(){
		fail("tbi");
	}
	
	public void as_a_connected_user_i_cannot_list_all_occupants_from_another_house(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_share_one_of_my_houses_to_another_person_by_mail(){
		fail("tbi");
	}

	public void as_a_user_i_can_enter_a_house_and_choose_an_occupant(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_visualize_the_house_highscores(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_visualize_all_tasks_done_by_users(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_list_all_possible_tasks(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_add_a_task_to_my_done_tasks_and_gain_the_associated_points(){
		fail("tbi");
	}

	public void as_an_nonhabitant_i_can_not_visualize_any_info_from_the_house(){
		fail("tbi");
	}

	public void as_an_nonhabitant_i_can_not_add_any_task_to_the_house(){
		fail("tbi");
	}

	@After
	public void cleanOccupants(){
		// Remove created occupants
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdOccupantsIds){
			HttpHelper.deleteResource(HOUSE+houseId+"/occupant/"+id, headers);
		}
		createdOccupantsIds = new ArrayList<String>();
	}

	@AfterClass
	public static void cleanHouses(){
		// Remove created houses
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, AUTH_KEY_ADMIN);
		for (String id : createdHouseIds){
			HttpHelper.deleteResource(HOUSE+id, headers);
		}
		createdHouseIds = new ArrayList<String>();
	}
}