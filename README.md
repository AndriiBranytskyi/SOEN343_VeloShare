# SOEN343_VeloShare
Repository for the SOEN343 HK-X


## Description
VeloShare is a webite that allows users to rent a bike from a current bike station and then return it after use. Users can rent a bike from any station and can return the bike to another station.

Phase 1
The goal of this phase is to:
- Define the problem and product scope.
- Provide a clear product position statement.
- Outline assumptions and dependencies.
- Design a **context diagram** and **domain model** to represent the system at a high level.

---

## Features 


---

## Technology & Tools
- **Version Control:** GitHub (private repository)  
- **Collaboration:** GitHub Issues for tasks/bugs, team coordination  
- **Diagramming:** draw.io
- **Languages:**
  - Backend: Java 
  - Frontend: JavaScript 

---

## A few instructions:
- Make sure you have Java 17 or +
- Install maven (check it with -> mvn -v)
- Run the backend: from src/main/java/com/veloshare/api/ApiApplication.java -> click "Run" on ApiApplication.main()
  Wait until it says "Tomcat started on port 8080"
  OR 
  Run with terminal from root -> run mvn spring-boot:run
- Open this link in browser: http://localhost:8080/UI/HomePage.html
- Register or Login using Firebase credentials -> you'll be redirected to http://localhost:8080/UI/index.html
- Top right corner should show your User ID so COPY IT
- ADD yourself as an operator by pasting the ID (since were all devs for now) in roles.json  
- RESTART BACKEND and RELOAD BROWSER.. you should now see operator dashboard 
**Note: anyone not in roles.json is automatically a Rider**


## Team members

- Andrii Branytskyi 40251093  
- Rita Kardachian 40283698  
- Nayla Nocera 40283927  
- Daniel Ayass 40232853  
- Jana El Madhoun 40272201  
- Jeremy Fung 40252404  
