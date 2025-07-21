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

## ⬇️ Downloads & Running the Application

You can download a single `.zip` file from the GitHub Releases that contains everything you need: the runnable `.jar` file, all necessary data files (`.txt` files), image assets, and the full NetBeans project source code.

#### Prerequisites:
* **Java Runtime Environment (JRE):** Ensure you have JRE 8 or higher installed to run the application.
* **Java Development Kit (JDK) & NetBeans IDE (Optional for developers):** If you plan to explore the source code or develop further, you'll need JDK 8+ and NetBeans IDE.

### How to Get Started:

1.  **Download Release:**
    * Go to the [Releases section](https://github.com/KhongCL/AHMFPS/releases) of this GitHub repository.
    * Download the latest `.zip` file (eg. `AHMFPS-v1.0.0.zip`).
2.  **Extract Files:**
    * Extract the contents of the downloaded `.zip` file to a folder on your computer. This will create the main project folder (e.g., `AHMFPS-v1.0.0`).

### Running the Application (For End-Users):

1.  **Navigate to Executable:**
    * Inside the extracted `AHMFPS` project folder, navigate to the `dist` sub-folder.
    * You will find the `AHMFPS-v1.0.0.jar` file here.
    * All required `.txt` data files and image assets are already placed correctly within this `dist` folder.
2.  **Run:**
    * **Graphical Interface:** Simply **double-click the `AHMFPS.jar` file** to launch the application.
    * **Command Line (Recommended for troubleshooting):**
        * Open your command prompt or terminal.
        * Navigate to the `dist` directory:
            ```bash
            cd path/to/extracted/AHMFPS/dist
            ```
        * Run the JAR file:
            ```bash
            java -jar AHMFPS.jar
            ```

### Opening the Project in NetBeans (For Developers):

There are two ways to open the project in NetBeans after downloading:

#### Option 1: Import Directly from Zip (Recommended for initial setup)

1.  **Launch NetBeans IDE.**
2.  Go to `File` -> `Import Project` -> `From Zip File...`.
3.  Navigate to and select the **downloaded `AHMFPS-v1.0.0.zip` file**.
4.  Follow the on-screen prompts to import the project. NetBeans will handle the extraction and setup.

#### Option 2: Open Extracted Project Folder

1.  **Ensure you have extracted the `AHMFPS-complete-v1.0.0.zip` file** (as described in "How to Get Started" step 2).
2.  **Launch NetBeans IDE.**
3.  Go to `File` -> `Open Project...`.
4.  Navigate to the **extracted root `AHMFPS` folder** (the one containing `src`, `build`, `nbproject`, etc. – it should have the NetBeans project icon) and click `Open Project`.

#### Run from IDE (After Opening/Importing):

* Once the project is open in NetBeans, go to `Run` -> `Run Project` (or press `F6`).

---

## 🔑 Initial Authentication Codes

To access the restricted areas of the system, use the following authorization codes on the respective login/registration pages:

* **Manager Authorization Code:** `AUTH789`
* **Staff Authorization Code:** `AUTH456`

### Pre-existing User Data:

For testing login and registration functionalities, you can refer to the `users.txt` file located in the `dist` folder. This file contains pre-populated user accounts with various roles (manager, staff, resident) and their respective usernames (3rd attribute) and passwords (4th attribute). You can use these credentials to log in and explore different user interfaces.

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
