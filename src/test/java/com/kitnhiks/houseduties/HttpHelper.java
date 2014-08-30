package com.kitnhiks.houseduties;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.with;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import com.jayway.restassured.response.Response;

public class HttpHelper {
	
	public static String BASE_URL = "http://127.0.0.1:8080/s/";
	public static String AUTH_KEY_HEADER = "X-AuthKey";
	public static String AUTH_KEY_ADMIN = "kukuku";
	private static String jsonContentType = "application/json; charset=utf-8";
	
	public static Response postResourceJson(String resource, String json, HashMap<String,String> headers){
		Response response = with().body(json).headers(headers).contentType(jsonContentType).post(resource);
		return response;
	}
	
	public static Response postResourceJson(String resource, String json){
		return postResourceJson(resource, json, new HashMap<String, String>());
	}
	
	public static Response getResource(String resource){
		return get(resource);
	}
	
	public static Response getResource(String resource, HashMap<String,String> headers){
		return with().headers(headers).get(resource);
	}
	
	public static Response getResourceWithJson(String resource, String json){
		return with().body(json).contentType(jsonContentType).get(resource);
	}
	
	public static Response deleteResource(String resource, HashMap<String,String> headers){
		Response response = with().headers(headers).delete(resource);
		return response;
	}
	
	public static void assertResponseStatusCode(int statusCode, Response response){
		assertEquals(statusCode, response.getStatusCode());
	}
}
