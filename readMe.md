💸 SplitWiseLite

SplitWiseLite is a simplified bill-splitting application designed to manage group expenses effortlessly. Built with a robust backend using Spring Boot and an interactive frontend in React, it ensures seamless tracking and splitting of expenses among group members.
📜 Table of Contents

    Features
    Technologies Used
    Architecture
    Backend Details
    Frontend Details
    Setup Instructions
    Screenshots
    Acknowledgments

🎯 Features

    Group Management: Create and manage groups, with dynamic addition of members.
    Expense Tracking: Add, view, and split expenses among group members.
    Balances and Settlements: Automatically calculate and display outstanding balances for easy settlements.
    Interactive UI: Modern design for a seamless user experience.
    Scalable Architecture: Backend API designed for scalability and future enhancements.

🛠️ Technologies Used
Backend:

    Framework: Spring Boot
    ORM: JPA with Hibernate
    Database: MySQL/PostgreSQL
    APIs: RESTful APIs
    Utilities: Lombok

Frontend:

    Framework: React
    State Management: React Hooks
    Styling: CSS/Material-UI
    HTTP Client: Axios

🏛️ Architecture

SplitWiseLite follows a clean, modular architecture:

    Backend:
    Implements a REST API that adheres to the Model-View-Controller (MVC) design pattern.

    Frontend:
    Single-page application built using React for dynamic interaction with the backend.

    Database Schema:
    Designed to support a many-to-many relationship between users and groups.

🔧 Backend Details

    Endpoints:
        /users: Manage user profiles.
        /groups: Create, view, and manage groups.
        /expenses: Add and track expenses within groups.

    Key Features:
        Lombok Integration: Reduces boilerplate code with annotations like @Getter and @Setter.
        Error Handling: Graceful handling of API errors using Spring Boot’s @ControllerAdvice.
        Many-to-Many Relationships: Efficiently managed with JPA.

🎨 Frontend Details

    Components:
        GroupList: Displays all groups and allows creation of new ones.
        ExpenseTracker: Shows expense history and allows adding expenses.
        BalanceView: Displays outstanding balances and settlements.

    Key Features:
        Axios Integration: Smooth communication with the backend.
        Material-UI: Enhanced user experience with prebuilt UI components.
        React Hooks: Simplified state and lifecycle management.

🚀 Setup Instructions
Prerequisites:

    Java (17 or higher)
    Node.js (16 or higher)
    MySQL/PostgreSQL
    Maven

Backend:

    Clone the repository:

git clone https://github.com/username/splitwise-backend.git  
cd splitwise-backend

Configure the database in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/splitwise  
spring.datasource.username=your_username  
spring.datasource.password=your_password

Build and run the project:

    mvn spring-boot:run  

Frontend:

    Clone the repository:

git clone https://github.com/username/splitwise-frontend.git  
cd splitwise-frontend

Install dependencies:

npm install

Start the development server:

    npm start  

