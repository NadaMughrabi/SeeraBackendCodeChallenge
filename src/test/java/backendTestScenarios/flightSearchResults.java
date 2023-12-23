package backendTestScenarios;

import org.testng.annotations.Test;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

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
								.statusCode(200)
								.extract();
	}
	
	/*API Request to fetch Search Results*/
	@Test(priority=2)
	void getFlightSearchResults()
	{
		String flightSearchResultURL = API_URL + FLIGHTS_SEARCH_RESULT_ENDPOINT;
		
		given()
			.contentType("application/json")
			.body(flightSearchMetadata.asString())
		.when()
			.post(flightSearchResultURL)
		.then()
			.statusCode(200)
			.log().all();
	}
}
