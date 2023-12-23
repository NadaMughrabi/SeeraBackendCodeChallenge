//Nada Mughrabi
package backendTestScenarios;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class flightSearchResults {
	
	private final String API_URL = "https://ae.almosafer.com/api/v3";
	private final String FLIGHTS_SEARCH_METADATA_ENDPOINT = "/flights/flight/search";
	private final String FLIGHTS_SEARCH_RESULT_ENDPOINT = "/flights/flight/async-search-result";
	private final String SAMPLE_FLIGHT_QUERY = "?query=RUH-JED/2023-12-25/2023-12-30/Economy/2Adult";
	
	private ExtractableResponse<Response> flightSearchMetadata;
	
	/* API Request to get Search Metadata 
	 * to be used to fetch search results
	 * */
	@Test(priority=1)
	void getFlightQueryMetaData()
	{
		String flightSearchMetadataURL = API_URL + FLIGHTS_SEARCH_METADATA_ENDPOINT + SAMPLE_FLIGHT_QUERY;
		
		flightSearchMetadata = get(flightSearchMetadataURL)
					.then()
					.extract();
		
		Assert.assertEquals(flightSearchMetadata.statusCode(), 200);
		
		String searchTypeResponce = flightSearchMetadata.jsonPath().get("request.searchType").toString();
		Assert.assertEquals(searchTypeResponce, "Roundtrip");
		
		String isRoundTripResponce = flightSearchMetadata.jsonPath().get("request.isRoundTrip").toString();
		Assert.assertEquals(isRoundTripResponce, "true");
		
		String preferredCabinResponse  = flightSearchMetadata.jsonPath().get("request.leg[0].preferredCabin").toString();
		Assert.assertEquals(preferredCabinResponse, "Economy");
		
		String originIdResponse = flightSearchMetadata.jsonPath().get("request.leg[0].originId").toString();
		Assert.assertEquals(originIdResponse, "RUH");	
		
		String destinationIdResponse = flightSearchMetadata.jsonPath().get("request.leg[0].destinationId").toString();
		Assert.assertEquals(destinationIdResponse, "JED");

		String departureResponse = flightSearchMetadata.jsonPath().get("request.leg[0].departure").toString();
		Assert.assertEquals(departureResponse, "2023-12-25");

		String paxResponse = flightSearchMetadata.jsonPath().get("request.pax.adult").toString();
		Assert.assertEquals(paxResponse, "2");
		
		String queryResponse = flightSearchMetadata.jsonPath().get("request.query").toString();
		Assert.assertEquals(queryResponse, "RUH-JED/2023-12-25/2023-12-30/Economy/2Adult");
	}
	
	/*API Request to fetch Search Results*/
	@Test(priority=2)
	void getFlightSearchResults()
	{
		String flightSearchResultURL = API_URL + FLIGHTS_SEARCH_RESULT_ENDPOINT;
		
		String nID = flightSearchMetadata.jsonPath().get("next.nid").toString();
		
		given()
			.contentType("application/json")
			.body(flightSearchMetadata.asString())
		.when()
			.post(flightSearchResultURL)
		.then()
			.header("Content-Type",equalTo("application/json"))
			.statusCode(200)
			.body("next.nid", equalTo(nID))
			.log().all();	
	}
}
