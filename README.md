# Vehicle Trading System (CSV)
This PoC-like project is the development of a new platform for Windows-systems to buy, sell and trade vehicles using ads solely written in Java using Postgres-DB as a backup system with high security standards.
The functionality is very similar to popular car trading sites like cars.com or mobile.de. Since this is development focuses on the backend functionality there is no graphical user interface. 
The interface uses the Windows-console on for user input and output.

Currently there is a second, less developed version available using CSV-files instead of the Postgres-database for storage.
You can find the repository <a href="https://github.com/AndrikSeeger/VehicleTradingSystem_CSV" target="_blank">here</a>.

## Language
Since the usage of the project was initially planned as exclusive for the German car market the documentation is also in German.
For further information and explanations in English please contact me.

## Startup
To execute the application you need <a href="https://www.oracle.com/java/technologies/downloads/#java16" target="_blank">JDK16 from Oracle</a> and <a href="https://www.postgresql.org/download/windows/" target="_blank">Postgres 13 for Windows</a>.
Having those two dependencies installed you can simply run `java -jar VehicleTradingSystem.jar` in your console. All other libraries like JDBC are included in the exported JAR-file.

### Database
After the initial startup of the system the Postgres-database that shall be used needs to be configured. Therefore you need to create a new database in Postgres. After that you can input the following parameters of the database into the console after the according input request to configure the system.
* URL | *Example: bc:postgresql://localhost:1234/test*
* Username | *Example: postgres*
* Password  | *Example: 1234*


The following initial menu after a successful input looks like this:
<p align="center">
<img src="https://raw.githubusercontent.com/AndrikSeeger/VehicleTradingSystem_CSV/master/Ressources/Startup.png"/>
</p>

## Usage
The usage of the program is pretty straight forward using numbers as input for the menu. The program has complete error handling for unexpected inputs with detailed messages and explanations.

### Close input
To close the current input and to return to the last opened menu just press enter two times at any given point in the program.

### Setup
The search of vehicles ("Fahrzeugsuche") according to given filtering and sorting parameters is possible without being logged in. For other actions like advertising a new car you need to create a user account and login to the system. There are two types of accounts: personal and professional. Every kind of input like the e-mail-address are checked for the correct syntax described in each input request.

### Vehicle search
You can search for vehicles by filtering the technical parameters of each vehicle. The resulting vehicles can be sorted in different orders.

#### Sorting
You can sort by:
* Price increasing
* Price decreasing
* Mileage increasing
* Mileage decreasing
* First registration of the vehicle increasing
* First registration of the vehicle decreasing
* Publishing date of the ad increasing
* Publishing date of the ad decreasing

#### Filtering
First you have to choose if you're searching for cars or trucks.

You can filter cars by:
* Brand
* Model
* Price
* Power
* Mileage
* Weight
* First registration of the vehicle
* Type of car
* Sports package build in

You can filter trucks by:
* Brand
* Model
* Power
* Mileage
* Weight
* First registration of the vehicle
* Maximum load
* Towing capacity
* Hydraulic system build in

## Testing
There are unit tests available in the build. The lines of code **test coverage is at 83,7%**.

## Documentation
For the database implementation please refer to the in-code documentation.
The full documentation of the <a href="https://github.com/AndrikSeeger/VehicleTradingSystem_CSV" target="_blank">CSV-version</a> with similar features was automatically created from the in-code documentation. It's available <a href="https://github.com/AndrikSeeger/VehicleTradingSystem_CSV/tree/main/Documentation" target="_blank">here</a> as linked HTML-Files, so it can be used like a website. 

For a deeper understanding of the implemented business logic and an abstract modelling of the program structure please refer to the <a href="https://github.com/AndrikSeeger/VehicleTradingSystem_CSV" target="_blank">CSV-version</a>.

## FAQ
* **Is there a POSIX-version (macOS/Linux) of the application using Postgres?**

    This version was developed for the use on Windows-systems. Since Postgres and the Java-Runtime are available for POSIX-like systems the usage on those is still possible with very few adaptions.

* **Is the maximum amount of accounts/vehicles limited?**
 
    No, the number of accounts and vehicles is not artificially limited in code but the memory capacity on your system is limited, hence the system gets slower if you surpass your available physical memory capacity.
    
* **Can I use the code for my project?**
 
    Yes, you're free to use the project code for closed projects. If you plan on publishing your project please reference my development.
    
* **Are additional features available in the future?**
 
    There are some experimental features like remote access which currently aren't published. A new version featuring those functionalities is planned. Please contact me for further information.

