# Projekt Inżynierski
Aplikacja internetowa do tworzenia sieci Petriego
## Wymagania
- [Node v14.17.3 (LTS)] (https://nodejs.org/en/blog/release/v14.17.3)
- [Yarn] (https://yarnpkg.com/)
- [Java SE 17.0.1] (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [MySQL 8.0] (https://dev.mysql.com/doc/relnotes/mysql/8.0/en/)
## Konfiguracja projektu
### Plik **PetriNetApp/springboot-backend/src/main/java/resources/application.properties**
Edytujemy dane bazy, logowania do bazy oraz smtp
```application.properties
spring.datasource.url= jdbc:mysql://localhost:3306/petri
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
server.port=8081
.
.
.
spring.datasource.username= root
spring.datasource.password=
.
.
.
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=
```
## Stawianie projektu
Za pomocą MySQL tworzymy bazę danych o nazwie "petri".  
Z poziomu **PetriNetApp/front** wykonujemy polecenia: 
```powershell
yarn
yarn serve
```
Z poziomu **PetriNetApp/springboot-backend** uruchamiamy serwer za pomocą środowiska np. IntelliJ IDEA.
