# Wallet Service

The Wallet Service is a component that allows users to manage their wallets and perform transactions. 
This repository contains the sample code for a simple credit/debit wallet along with the basic interface.

## Getting Started

These instructions will guide you on how to set up and use the Wallet Service along with the UI.

Note: The Wallet Service is based on the Quarkus framework.

### Prerequisites

To run the Wallet Service and UI, make sure you have the following installed:

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- MongoDB
- Web browser (for accessing the UI)

### Installation

1. Clone the repository:

```shell
git clone https://github.com/ManishaYadav15/transaction.git
```

2. Navigate to the project directory:

```shell
cd transaction_be
```

3. Build the project using Maven:

```shell
mvn clean install
```

### Configuration

The Wallet Service requires a MongoDB database connection. Update the database connection properties in the `application.properties` file located in the `src/main/resources` directory:

```
quarkus.mongodb.connection-string=mongodb://localhost:27017/dbname
```

Make sure to provide the correct MongoDB connection URL.

Note: Before running the Wallet Service, make sure you have created the MongoDB database required for the application. 

### Usage

The Wallet Service exposes a RESTful API to manage wallets and perform transactions. Additionally, there is a user signup/login functionality provided through the UI.

To run the Wallet Service locally for development purposes, follow these steps:
1. Open a terminal or command prompt.
2. Navigate to the project directory:

```shell
cd transaction_be
```

3. Build and run the application using the Quarkus development mode with the following command:
   
```shell
mvn quarkus:dev
```

This command starts the Wallet Service in development mode, where it automatically reloads the application when changes are detected.

Once the application is running, you can access the API endpoints programmatically and the UI through your web browser.

Note: By default, Quarkus runs on the address localhost:8080

Use the provided API endpoints (e.g., /wallet/{username}) to interact with the Wallet Service programmatically.

#### User Signup/Login

To use the UI and access the wallet functionality, follow these steps:

1. Open the `transaction_fe/login_signup.html` file in a web browser.

2. On the login/signup page, create a new user account by providing the required information or log in with an existing account.

3. Once logged in, you will have access to the wallet functionality through the UI.

   - Enter the amount to credit or debit in the respective input field.
   - Click on the "Credit" button to add funds to the wallet.
   - Click on the "Debit" button to deduct funds from the wallet.

#### Get Wallet Balance

Retrieve the balance of a user's wallet.

```
GET /wallet/{username}
```

Example response:

```json
{
  "username": "johnDoe",
  "balance": 500.0
}
```

If the wallet for the given username is not found, the API will return a `404 Not Found` response.

#### Error Handling

If an error occurs during API requests or UI interactions, appropriate error messages will be displayed on the UI.

- `404 Not Found`: The requested resource (wallet) was not found.
- `500 Internal Server Error`: An unexpected error occurred.

### Testing

Unit tests for the Wallet Service are available in the `src/test` directory. You can run the tests using the following command:

```shell
mvn test
```

### Demo

Watch a demo of the Wallet Service functionality in action:

[Demo Video](https://drive.google.com/file/d/1mp_IYARrEugMlMJ71jnL9X-R6mM41Hjl/view?usp=drivesdk)

In this video, you can see how to sign up, log in, and perform transactions using the Wallet Service. It provides a visual representation of the application's features and can help you understand the workflow.
