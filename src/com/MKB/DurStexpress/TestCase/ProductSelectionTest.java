package com.MKB.DurStexpress.TestCase;

import static io.restassured.RestAssured.given;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.MKB.Utilities.ReusableMethod;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ProductSelectionTest {

	@Test
	public void productDetails() {

		try {
			// Create a new file output stream.
			PrintStream fileOut = new PrintStream("./Output_Logs.txt");

			// Create a new file error stream.
			PrintStream fileErr = new PrintStream("./Error_Logs.txt");

			// Redirect standard out to file.
			System.setOut(fileOut);

			// Redirect standard err to file.
			System.setErr(fileErr);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("\n*****----->Step1: Get all Product Details:<----******\n");
		RestAssured.baseURI = "http://localhost:3030";
		Response res = 
				 given().log().all()
				.when().get("/products/")
				.then().assertThat().statusCode(200)
				.and().contentType(ContentType.JSON)
				.and().log().body().extract().response();

		JsonPath js = ReusableMethod.rawToJson(res);

		System.out.println("\n*****----->Step2:Extracting all Product ID from Response body Json:<----******");
		int count = js.get("data.size()");
		System.out.println("\n Total Number of product available: " + count);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < count; i++) {
			list.add(js.get("data[" + i + "].id"));
		}
		System.out.println("\n List of product IDs : " + list);

		System.out.println(
				"\n*****----->Step3: Select ID of first Product(optional and can select as per choice) from product ID list:<----******");
		int product_id = list.get(0);
		
		System.out.println("\n ID for first Product :" + product_id);

		System.out.println("\n*****----->Step4: Get Details of of first Product using product ID:<----******\n");
		int Product_ID_Sending_with_Request = product_id;
		Response res1 = 
				 given().log().all()
				.when().get("/products/" + product_id++)
				.then().assertThat().statusCode(200)
				.and().contentType(ContentType.JSON)
				.and().log().body().extract().response();
		JsonPath js1 = ReusableMethod.rawToJson(res1);

		System.out.println(
				"\n*****----->Step5: Verifying product ID of sent request and received Response :<----******\n");
		int Product_ID_Received_From_Response = js1.getInt("id");
		Assert.assertEquals(Product_ID_Received_From_Response, Product_ID_Sending_with_Request);
		System.out.println("\n Congratulations: you have successfully retrieved a product by using product ID from BestBuy API playground\n");

	}
}
