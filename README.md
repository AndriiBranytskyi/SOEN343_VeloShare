# SOEN343_VeloShare
Repository for the SOEN343 HK-X

## Description
VeloShare is a website that allows users to temporarily rent a bike or e-bike for personal usage.

## Instructions to run
1. Prerequisits: Java 17+, Apache Maven
2. Dowload project for GitHub
3. Navigate to root folder and build the project: "mvn clean install"
4. Run the backend server with "mvn spring-boot:run"
5. In browser go to http://localhost:8080/UI/HomePage.html

## Technology & Tools
- **Version Control:** GitHub   
- **Collaboration:** GitHub Issues for tasks/bugs, team coordination, Discord 
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
