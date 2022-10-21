package api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class PostsTests {
String token;
String bookingID;
   @BeforeClass
    public void login(){
       String url="https://restful-booker.herokuapp.com/auth";
       String body= """
               {
                   "username" : "admin",
                   "password" : "password123"
               }
               """;
       ValidatableResponse validatableResponse=given().body(body)
               .header("Content-Type","application/json")
               .when().post(url).then();
       Response response=validatableResponse.extract().response();
       JsonPath jsonPath=response.jsonPath();
       token=jsonPath.getString("token");
       System.out.println(token);


   }
@Test(priority = 0)
   public void createBookingTest(){
      String url="https://restful-booker.herokuapp.com/booking";
      String body= """
              {
                  "firstname" : "Jim",
                  "lastname" : "Brown",
                  "totalprice" : 111,
                  "depositpaid" : true,
                  "bookingdates" : {
                      "checkin" : "2018-01-01",
                      "checkout" : "2019-01-01"
                  },
                  "additionalneeds" : "Breakfast"
              }
              """;
      ValidatableResponse validatableResponse=given().body(body)
              .header("Content-Type","application/json").when()
              .post(url).then();
      validatableResponse.assertThat()
           .statusCode(HttpStatus.SC_OK)
           .body("booking.firstname", equalTo("Jim"))
           .body("booking.lastname", equalTo("Brown"))
           .body("booking.depositpaid", equalTo(true));
   Response response=validatableResponse.extract().response();


   JsonPath jsonPath=response.jsonPath();
    bookingID=jsonPath.getString("bookingid");


}
@Test(priority = 1,dependsOnMethods = "createBookingTest")
   public void updateBookingTest(){
      String url="https://restful-booker.herokuapp.com/booking/"+bookingID;
      String body= """
              {
                  "firstname" : "James",
                  "lastname" : "Brown",
                  "totalprice" : 111,
                  "depositpaid" : true,
                  "bookingdates" : {
                      "checkin" : "2018-01-01",
                      "checkout" : "2019-01-01"
                  },
                  "additionalneeds" : "Breakfast"
              }
              """;
      ValidatableResponse validatableResponse=
              given().body(body).header("Content-Type","application/json").header("Cookie","token="+token).
                      header("Authorisation","Basic")
                      .put(url).then();
   validatableResponse.assertThat()
           .statusCode(HttpStatus.SC_OK)
           .body("firstname", equalTo("James"))
           .body("lastname", equalTo("Brown"))
           .body("depositpaid", equalTo(true));


}
@Test(priority =2)
   public void getBooking(){
   String url="https://restful-booker.herokuapp.com/booking/"+bookingID;

   ValidatableResponse validatableResponse=
           given().header("Accept","application/json")
                   .get(url).then();
   validatableResponse.assertThat()
           .statusCode(HttpStatus.SC_OK)
           .body("firstname", equalTo("James"));

}
@Test(priority = 3)
   public void deleteBooking(){
   String url="https://restful-booker.herokuapp.com/booking/"+bookingID;

   ValidatableResponse validatableResponse=
           given().header("Content-Type","application/json").header("Cookie","token="+token).
                   header("Authorisation","Basic")
                   .delete(url).then();
   validatableResponse.assertThat()
           .statusCode(HttpStatus.SC_OK);



}
}
