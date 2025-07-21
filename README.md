# APU Hostel Management Fees Payment System (AHMFPS)

The APU Hostel Management Fees Payment System (AHMFPS) is a software solution developed in Java, designed to streamline the management of hostel-related fees for Asia Pacific University's hostel residents. Leveraging Java's object-oriented principles, the system ensures modularity, reusability, and scalability.

The primary objective of this project is to address the challenges faced by hostel management, staff, and residents in handling registration, fee payments, and account updates efficiently. By automating these processes, AHMFPS aims to reduce manual errors, improve record management, and enhance overall user experience.

---

## ✨ Key Features

AHMFPS provides a robust set of functionalities categorized by user roles and system operations:

### For Residents:
* **Secure Account Access:** Register and log in with unique credentials.
* **Account Management:** Update personal information and view payment history.
* **Booking Management:** Make new room bookings and view booking details.
* **Payment Tracking:** Access records of all processed payments.

### For Staff:
* **Secure Account Access:** Log in with valid credentials and an authorization key.
* **Payment Finalization:** View pending payments and finalize transactions for residents.
* **Receipt Generation:** Generate official receipts for successful payments.
* **Account Management:** Update their own personal account details.

### For Managers:
* **Secure Account Access:** Log in with valid credentials and an authorization key.
* **User Account Management:** Search, update, suspend, restore, and delete user accounts (residents, staff, and other managers).
* **Registration Approval:** Approve new account registrations for all user roles.
* **Room Management:** Add new rooms, update room availability/occupancy status, and delete/restore rooms (cannot delete rooms currently in use).
* **Fee Rate Management:** Set and update hostel fee rates for different room types (Standard, Large, Family) and durations (daily, weekly, monthly, yearly).
* **Account Management:** Update their own personal account details.

### System-Wide Functionalities:
* **Authentication & Authorization:** Role-based access control ensuring secure and appropriate access to functionalities.
* **Payment Processing:** Supports digital and in-person (cash) payment methods (Credit Card, Bank Transfer, Cash).
* **Payment Status Tracking:** Tracks booking payment statuses from "unpaid" to "pending" to "paid," and updates room status accordingly.
* **Input Validation:** Minimizes data entry errors for reliable system operation.
* **User-Friendly Interface:** Provides a simple and intuitive interface for all users.
* **Error Handling:** Displays informative pop-ups for invalid actions.

---

## 🚀 Technologies Used

* **Language:** Java
* **Database:** Data stored in text files
* **Development Environment:** Java Development Kit (JDK)

---

## 💻 Local Setup Instructions

To run the APU Hostel Management Fees Payment System locally, you will need a Java Development Kit (JDK) installed on your machine.

### Prerequisites

* **Java Development Kit (JDK):** Ensure you have JDK 8 or higher installed. You can download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/install/).

### Installation Steps

Follow these steps to set up AHMFPS on your local machine:

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/KhongCL/AHMFPS.git
    cd AHMFPS
    ```

2.  **Open in an IDE:**
    * Open the `AHMFPS` project folder in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse, NetBeans).
    * The IDE should automatically detect the project structure and dependencies.

3.  **Compile and Run:**
    * Locate the main class (e.g., `Main.java` or `App.java`) that contains the `public static void main(String[] args)` method.
    * Run the main class directly from your IDE.

*(Optional: If your project builds into a JAR file)*

4.  **Run from JAR (if applicable):**
    * After building the project (e.g., using Maven or Gradle, or your IDE's build tools), navigate to the `target` or `build` directory where the `.jar` file is located.
    * Run the application from your terminal:
        ```bash
        java -jar YourProjectName.jar
        ```

---

### 📸 Application Screenshots

#### **Resident: Make Booking UI**
<img width="979" height="735" alt="image" src="https://github.com/user-attachments/assets/db8cbef2-31a3-4bca-a70a-1c0478a12d6f" />

#### **Staff: View Receipt UI**
<img width="979" height="736" alt="image" src="https://github.com/user-attachments/assets/79a3d3b5-819a-4309-8c31-774cb97a295f" />

#### **Manager: Manage Room UI**
<img width="979" height="740" alt="image" src="https://github.com/user-attachments/assets/e7d7ec4b-2ce3-430e-a2f1-d2e5d015ec70" />

---

## 📚 Further Information

For more detailed information regarding AHMFPS, including comprehensive design documents, assumptions, and implementation details, please refer to the full project documentation.

---

## 👥 Team

Developed by [Your Team Name/Members if applicable, e.g., "The AHMFPS Development Team"]

---

*Thank you for exploring the APU Hostel Management Fees Payment System!*
