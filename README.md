
# AL Carbon Calculator API

## Overview

The AL Carbon Calculator API is a RESTful service built with Java, Spring Boot, and MongoDB, designed to calculate a user's carbon footprint based on their energy consumption, transportation, and solid waste production. The API provides endpoints for starting the calculation, updating the information, and retrieving the results.

This API calculates the carbon footprint using emission factors stored in the database for energy consumption, transportation, and solid waste. It offers the following endpoints:

## Endpoints

### [POST] /open/start-calc

This endpoint starts a new carbon calculation. It takes the user's basic information (name, email, phone number, and state) and stores it in the database. It returns a unique calculation ID that will be used for future operations.

#### Request Body:
```json
{
  "name": "USER TESTER",
  "email": "user.tester@example.com",
  "uf": "RJ",
  "phoneNumber": "0123456789012"
}
```

#### Response:
```json
{
  "id": "calculationId"
}
```

### [PUT] /open/info

This endpoint allows updating the carbon calculation data. It takes energy consumption, transportation details, and solid waste production to update the stored calculation data for a user.

If this endpoint is called a second time for the same ID, it will overwrite the existing data.

#### Request Body:
```json
{
  "id": "calculationId",
  "energyConsumption": 400,
  "transportation": [
    {
      "type": "CAR",
      "monthlyDistance": 600
    },
    {
      "type": "MOTORCYCLE",
      "monthlyDistance": 300
    }
  ],
  "solidWasteTotal": 150,
  "recyclePercentage": 0.5
}
```

#### Response:
```json
{
  "success": true
}
```

### [GET] /open/result/{id}

This endpoint retrieves the carbon footprint result for the provided calculation ID.

#### Response:
```json
{
  "energy": 322.62,
  "transportation": 209.13,
  "solidWaste": 245.28,
  "total": 777.03
}
```

## Calculation Logic

The carbon footprint is calculated using the following emission factors:

### Energy Consumption:
- Emission factor for each Brazilian state (UF) is used to calculate the carbon emission based on the user's energy consumption.
- **Formula**: `Carbon emission = energy consumption * emission factor`

### Transportation:
- Emission factor for each type of transportation is used to calculate the carbon emission based on the distance traveled.
- **Formula**: `Carbon emission = distance * transportation type emission factor`

### Solid Waste:
- Emission factors for recyclable and non-recyclable solid waste are used to calculate the carbon emission.
- **Formula**: `Carbon emission = solid waste production * emission factor`

## Running the Application

To run the AL Carbon Calculator API locally, follow the steps below:

1. Clone the repository:
   ```bash
   git clone https://github.com/devfelixarthur/CarbonCalc.git
   cd CarbonCalc
   ```

2. Build and run the application:
    - For **Maven**, run:
      ```bash
      mvn spring-boot:run
      ```
    - For **Gradle**, run:
      ```bash
      ./gradlew bootRun
      ```

3. The application will be running at `http://localhost:8085`.

4. Swagger documentation is available at: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## Database

The MongoDB database can be started using Docker Compose. The database is populated with initial data, including emission factors for energy consumption, transportation, and solid waste.

To start the MongoDB database, run:
```bash
docker-compose up
```

To reset the database to its initial state, run:
```bash
docker-compose down -v
```

## Postman Collection

A Postman collection is available in the `postman` folder. Import the collection into Postman to easily test the API endpoints.

## Dependencies

- **Spring Boot**: The main framework used for the API.
- **Spring Data MongoDB**: For interacting with MongoDB.
- **Swagger**: For API documentation.
- **Lombok**: To reduce boilerplate code.

## License

This project is licensed under the MIT License.