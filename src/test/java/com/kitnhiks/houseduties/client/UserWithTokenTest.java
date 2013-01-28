package com.kitnhiks.houseduties.client;

import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_ADMIN;
import static com.kitnhiks.houseduties.HttpHelper.AUTH_KEY_HEADER;
import static com.kitnhiks.houseduties.HttpHelper.BASE_URL;
import static com.kitnhiks.houseduties.ModelHelper.assertJsonIsHouse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.kitnhiks.houseduties.HttpHelper;

public class UserWithTokenTest{

	private static final String HOUSE = BASE_URL+"house";
	private ArrayList<String> createdHouseIds = new ArrayList<String>();

	@Test
	public void as_a_user_i_can_retrieve_a_house_with_its_given_id(){
		// Create a new house
		String newHouse = "{\"name\":\"TEST_HOUSE\", \"password\":\"TEST_HOUSE_PWD\"}";
		Response postHouseResponse = HttpHelper.postResourceJson(HOUSE, newHouse);
		// Id
		String id = new JsonPath(postHouseResponse.asString()).getString("id");
		createdHouseIds.add(id);
		
		// Token 
		String token = postHouseResponse.header(AUTH_KEY_HEADER);

		// Retrieve created house
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put(AUTH_KEY_HEADER, token);
		Response getHouseResponse = HttpHelper.getResource(HOUSE+"/"+id, headers);

		// house ?
		assertJsonIsHouse(new JsonPath(getHouseResponse.asString()));
	}

	public void as_a_connected_user_i_can_add_an_occupant_to_my_house(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_leave_a_house(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_delete_a_house(){
		fail("tbi");
	}

	public void as_an_occupant_i_can_share_one_of_my_houses_to_another_person_by_mail(){
		fail("tbi");
	}

	public void as_a_user_i_can_enter_a_house_and_choose_an_occupant(){
		fail("tbi");
	}

	public void as_a_user_i_can_enter_a_house_and_create_a_new_occupant(){
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