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
* **Development Environment:** NetBeans IDE
* **Data Storage:** Text Files

---

## 💻 Local Setup (From Source Code in NetBeans)

To set up and run the AHMFPS application directly from its source code in NetBeans:

### Prerequisites

* **Java Development Kit (JDK):** Ensure you have JDK 8 or higher installed. You can download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/install/).
* **NetBeans IDE:** Install NetBeans IDE.

### Installation Steps

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/KhongCL/AHMFPS.git
    cd AHMFPS
    ```

2.  **Open in NetBeans:**
    * Open NetBeans IDE.
    * Go to `File` -> `Open Project...`
    * Navigate to the `AHMFPS` folder you just cloned and click `Open Project`.

3.  **Run the Application:**
    * In NetBeans, go to `Run` -> `Run Project` (or press `F6`).
    * The application's main window should appear.

---

## 📦 Distribution (Running the Executable JAR)

For users who want to run the application without setting up a development environment, a pre-built executable `.jar` file will be provided in the GitHub Releases section of this repository.

### Prerequisites

* **Java Runtime Environment (JRE):** Ensure you have JRE 8 or higher installed.

### How to Run:

1.  **Download the Release:**
    * Go to the [Releases section](https://github.com/KhongCL/AHMFPS/releases) of this GitHub repository.
    * Download the latest `.zip` file containing the application.

2.  **Extract Files:**
    * Extract the contents of the downloaded `.zip` file to a folder on your computer.

3.  **Place Data Files:**
    * **Important:** Ensure that the `.txt` data files (e.g., `users.txt`, `rooms.txt`, etc., are placed **in the same directory** as the `AHMFPS.jar` file.

4.  **Run the Application:**
    * **Graphical Interface:** Double-click the `AHMFPS.jar` file.
    * **Command Line (Recommended for troubleshooting):**
        * Open your command prompt or terminal.
        * Navigate to the directory where you extracted the files:
            ```bash
            cd path/to/extracted/folder
            ```
        * Run the JAR file:
            ```bash
            java -jar AHMFPS.jar
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
