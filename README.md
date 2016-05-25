# TicketService
TicketServiceApplication
assumptions#Notes#

1)Seatavailability are only based on no of seats available (no rows involved) and I have multiplied 
rows with seats in each row and tabulated as total seats in tables

2)Maxlevel is 1 and Minlevel is 4 

3)If user selects (maxlevel<minlevel)(1<3) then system try to hold seats in the maxlevel if it is 
available other wise it increment by 1 till it reaches minlevel and hold available seats in that range 
of levels if not found send response as cant hold and wont assign any level to that user

4)If user selects (maxlevel>minlevel) then system will only check for minlevel seats availability and 
hold them if they are available else send response as cant hold and wont assign any level to that 
user

5) If user select (maxlevel=minlevel) then it chooses one since both are same and do selection as 
available or not available

6) If user does not select any level then system start iterating the levels from 4 to 1 what ever 
available seats are found it will allocate accordingly to user

7) As soon you start the server with cmd (mvn spring-boot:run) it loads the data to inbuilt memory,
and you are good to go for calling endpoints . I will create WADL for the project that will be 
attached in the email

8) When ever user confirms the held seats status of the row will be changed to reserved and as a 
response send back the reservation id

How to run#
The project is built on spring-boot
use cmd (mvn clean install) for creating the war file, it runs all testcases, compiles code and creates war
use cmd (mvn spring-boot:run) for running the server 
