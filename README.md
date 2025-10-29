# ***FakeStoreAPI Automation Framework***

A comprehensive Rest Assured-based API Automation Framework designed for testing FakeStore API endpoints with a focus on reliability, scalability, and detailed reporting.

# ***Features***

### **Framework Architecture**

•	Rest Assured framework built from scratch for API testing with modular design patterns ensuring easy maintenance and extensibility.

•	POJO classes implemented for clean payload handling, improving code readability and reusability across test scenarios.

### **Test Coverage & Automation**

•	Comprehensive API test automation covering user, product, cart, and login endpoints with complete CRUD operations (Create, Read, Update, Delete).

•	Data validation and schema validation implemented to ensure API responses meet expected formats and business rules.

•	Data-driven testing capabilities using JSON files combined with TestNG DataProviders for dynamic test execution across multiple datasets.

### **Reporting & Logging**

•	Dual reporting setup with Extent Reports and Allure Reports, generating interactive HTML reports and JSON artifacts for detailed test analysis.

•	Comprehensive logging mechanism capturing API requests, responses, and test execution flow for effective debugging.

### **Test Execution**

•	TestNG XML configuration enables parallel test execution and selective test suite runs, optimizing test execution time and resource utilization.

•	Support for running specific test groups, priorities, and dependencies through TestNG annotations.

### **Automated Test Scenarios**

### User Endpoints

•	Get all users

•	Get single user by ID

•	Create new user

•	Update existing user

•	Delete user

•	User data validation

### Product Endpoints

•	Get all products

•	Get single product by ID

•	Get products by category

•	Create new product

•	Update existing product

•	Delete product

•	Product schema validation

### Cart Endpoints

•	Get all carts

•	Get single cart by ID

•	Get user carts

•	Create new cart

•	Update cart items

•	Delete cart

•	Cart data validation

### Authentication

•	User login validation

•	Token generation and verification

•	Authentication error handling

### ***Technologies Used***

•	Rest Assured - API testing library

•	TestNG - Testing framework

•	Java - Programming language

•	Maven - Build automation tool

•	Extent Reports - Interactive HTML reporting

•	Allure Reports - Advanced test reporting

•	Jackson/Gson - JSON processing


### **Reports**

![Report](reports/Screenshot%202025-10-29%20153106.png)

![Report](/reports/Screenshot%202025-10-29%20153226.png)

![Report](/reports/Screenshot%202025-10-29%20155038.png)


