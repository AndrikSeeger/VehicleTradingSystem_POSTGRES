<!--Copyright Andrik Seeger 2022-->

# 🚗 Vehicle Trading System (POSTGRES)

This PoC-like project is the development of a new platform for **Windows systems** to buy, sell and trade vehicles using ads solely written in **Java** using **Postgres-DB** as a backup system with high security standards.
The functionality is very similar to popular car trading sites like cars.com or mobile.de.
Since this development focuses on the backend functionality there is **no graphical user interface**.
The interface uses the **Windows console** for user input and output.

🔁 Currently there is a second, less developed version available using **CSV files** instead of the Postgres database for storage.
You can find the repository [here](https://github.com/AndrikSeeger/VehicleTradingSystem_CSV).

---

## 🗣️ Language

Since the usage of the project was initially planned as exclusive for the German car market the documentation is also in German.
For further information and explanations in English please contact me.

---

## ⚙️ Startup

To execute the application you need:

* [JDK 16 from Oracle](https://www.oracle.com/java/technologies/downloads/#java16) ☕
* [Postgres 13 for Windows](https://www.postgresql.org/download/windows/) 🐘

Having those two dependencies installed, you can simply run:

```bash
java -jar VehicleTradingSystem.jar
```

📦 All other libraries like JDBC are included in the exported JAR file.

---

### 🗃️ Database

After the initial startup, the Postgres database must be configured:

1. Create a new database in Postgres.
2. Input the following parameters into the console when prompted:

* **URL** | *Example: bc\:postgresql://localhost:1234/test*
* **Username** | *Example: postgres*
* **Password** | *Example: 1234*

🖥️ The following initial menu after a successful input looks like this:

<p align="center">
<img src="https://raw.githubusercontent.com/AndrikSeeger/VehicleTradingSystem_POSTGRES/master/Ressources/Startup.png"/>
</p>

---

## 🧭 Usage

The usage of the program is pretty straightforward using numbers as input for the menu.
The program has complete error handling for unexpected inputs with detailed messages and explanations.

---

### 🔚 Close input

To close the current input and return to the last opened menu, just press **Enter twice** at any given point.

---

### 🛠️ Setup

The **search of vehicles** ("Fahrzeugsuche") with filters and sorting is possible **without being logged in**.
To **advertise a new car**, you need to create an account and log in.
There are two account types: **personal** and **professional**.
Each input field (e.g., email address) is checked for correct syntax as described during input.

---

### 🔍 Vehicle search

You can search for vehicles by filtering the technical parameters of each vehicle.
The results can be **sorted** in different orders.

#### 📊 Sorting

* Price increasing / decreasing
* Mileage increasing / decreasing
* First registration increasing / decreasing
* Publishing date increasing / decreasing

#### 🎯 Filtering

First choose whether you're searching for **cars** or **trucks**.

**Cars** can be filtered by:

* Brand, Model
* Price, Power, Mileage, Weight
* First registration
* Type of car
* Sports package built in

**Trucks** can be filtered by:

* Brand, Model
* Power, Mileage, Weight
* First registration
* Maximum load
* Towing capacity
* Hydraulic system built in

---

## ✅ Testing

There are unit tests available in the build.
🧪 The lines of code **test coverage is at 75.7%**.
The tests for the main logic (excluding the database layer) are available [here](https://github.com/AndrikSeeger/VehicleTradingSystem_POSTGRES/tree/main/Tests).

---

## 📚 Documentation

For the **database implementation**, please refer to the in-code documentation.
The full documentation of the [CSV version](https://github.com/AndrikSeeger/VehicleTradingSystem_CSV) with similar features was automatically created from the in-code documentation.
It is available [here](https://github.com/AndrikSeeger/VehicleTradingSystem_CSV/tree/main/Documentation) as linked HTML files and can be used like a website.

For a deeper understanding of the implemented **business logic** and abstract **program structure**, please refer to the [CSV version](https://github.com/AndrikSeeger/VehicleTradingSystem_CSV).

---

## ❓ FAQ

**Is there a POSIX-version (macOS/Linux) of the application using Postgres?**
🧠 This version was developed for Windows. However, since Postgres and the Java Runtime are available for POSIX-like systems, running it there is possible with minor adjustments.

**Is the maximum amount of accounts/vehicles limited?**
No, but the system performance depends on your available memory. If you exceed it, the system will slow down.

**Can I use the code for my project?**
🆓 Yes, for closed/private use without restriction.
If publishing your project, please credit this development.

**Are additional features available in the future?**
🧪 Some experimental features like **remote access** are in development.
A new version featuring those functionalities is planned — contact me for more info.
