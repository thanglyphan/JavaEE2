PG6100 Exam.

Extra functionalities:

- Quiz can have more questions and answers. Meaning, at first my quiz is just a plain entity with a question and some answers(plain strings). Thats so booring. So i made a little more. Now my quiz(entity) holds Question(entity) and Answer(entity) while the Answer extends an entity knowing the solution, Solution(entity). I had some extra time so i manage to update this REST-service. Ok, so when a quiz is made, it have no questions. The user have to add thos questions in, here is my other API, called "qa"(questions and answers). The "qa" have those methods: 
- POST(add question to quiz)
- GET(get the question and answers by id)
- PATCH(update the question text)
- GET(get answer to question by id)
So my quiz can have one or 100 question inside. I didn't want to only have one question per "quiz", thats booring, so i upgrade a little by adding those things.

- And in the categories i have implemented pagination when returning a collection. You asked for the quizzes, why not do it in the categories as well.

- In gameRest I dont have any new DTO's. I use the old one, and its working fine. Added the dependency i needed, and just start using it. I use "QuestionDTO" because it have everything i need to implement my stuff.

- And to run everything locally when the dropwizard speaking to rest api, do it like this:
1. open terminal and cd to project(PG6100Exam).
2. type: mvn clean install
3. type: mvn wildfly:run
4. go in to "GameRest.java", uncomment line 21, comment line 22.
5. open second terminal and cd to project -> cd gameRest
6. type: mvn package
7. finally type: 
java -Ddw.server.applicationConnectors[0].port=9090 -Ddw.server.adminConnectors[0].port=9091 -jar target/gameRest-0.0.1-SNAPSHOT.jar server

I'm sure you know those things, but i wanted to show you that i know it too :)

AND!

- In task E2, there is /subcategories. And in the beginning of task E3 you say: In the “quizImp” module, you need to provide concrete implementations of the “CategoryRest” and “QuizRest” interfaces defined in “quizApi” module. When i see the /subcategories, i immediatly thinking "here i need to make a new interface for it", so i did. And "The “/categories/{id}/subcategories” endpoint should do a permanent redirect toward
“/subcategories?parentId=id”.", tells me i should deprecate that method, so I did.

- Thang Phan

