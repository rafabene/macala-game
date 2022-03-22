# Mancala Game - with Spring Boot or Microprofile :) 
Implementation of Macala game


You can find some visual explanations of the game rules by running a [Google Search](https://www.google.com/search?q=mancala+game) for Mancala or Kalaha game.

This implementation uses two parts to work.

## 1 - Java Backend

There are two backends. Both of them are a really thin layer that exposes the `Game domain` to the external world through websockets.

This help us to see that the web framework here is not important as long as you excercise the [Object-oriented programming](https://en.wikipedia.org/wiki/Object-oriented_programming) concepts.

 - [Microprofile Backend](./backends/microprofile-backend/) - Implemented using the [MicroProfile](https://microprofile.io/) specification and [Helidon](https://helidon.io/) implementation.

 - [Spring Boot Backend](./backends/springboot-backend/) - Implemented using [Spring Boot](https://spring.io/projects/spring-boot) and Websockets.

### 1.1 - Game domain

 Both web applications uses the game logic implemented with pure OOP.

 The only external dependency is the usage of the annotation [@JsonbTransient](https://javaee.github.io/javaee-spec/javadocs/javax/json/bind/annotation/JsonbTransient.html) to avoid the `internal board` from the [Game](./backends/game-domain/src/main/java/com/rafabene/mancala/domain/game/Game.java) class to be serialized to Json.

 From Eric Evans's [excellent book Domain Driven Design](https://www.amazon.com/gp/product/0321125215/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0321125215&linkCode=as2&tag=martinfowlerc-20):

    "Domain Layer (or Model Layer): Responsible for representing concepts of the business, information about the business situation, and business rules. State that reflects the business situation is controlled and used here, even though the technical details of storing it are delegated to the infrastructure. This layer is the heart of business software."


## 2 - HTML Frontend

Implemented using just HTML and Javascript. [NodeJS](https://nodejs.org/en/) has been used just to serve the static content. Later `nodejs` will be used to parameterize the backend address. 

To execute this game you need to execute both parts in the same host.

## Instructions do run the game locally

### Backend: 

Compile and package the game using [Maven](https://maven.apache.org/). 

This will build, test and package both implementations (`Spring-Boot`and `Microprofile`)

    cd backends
    mvn clean package

Choose your favorite implentation

    cd microprofile-backend/

or

    cd springboot-backend/

Now you can start the backend.

    java -jar target/mancala-game.jar

Done! Now you can start the frontend.

### Frontend

Install the required node packages using [NPM](https://www.npmjs.com/)

    npm install

Start the aplication:

    npm start


## Game instructions

Player 1: Open your browser at <http://localhost:3000/>.

Player 2: Open another tab (or browser) at <http://localhost:3000/> in the same computer.

Once that both players are connected. Hit the `Start Game` button.


## Improvements needed:

 - Modify the `frontend` to consume the backend from a parameterized address, so it can be deployed in a public address.


## FAQ

### - Why do you use Websockets?
Because it's a game, and the state of the Game (and its pieces like Players, Board, etc) should be immediatly reflected for all players (and even viewers).

### - Could you use REST?
Yes, but this is a web Game, not an API to a Game. This disctinction needs to be very clear. 

### - Why `Spring Boot` or why `Microprofile`?
Because it shows that the web technology doesn't matter as long as the domain is well abstracted.

### - Is this architecture suitable for `enterprise applications`?
It depends. There could be certain use cases that requires you to watch some business gauges near to real time. In that case Websockets is preferable than a REST API.

### - Where is your `Service` class?
This implementation doesn't not need/use a service class as all the logic is part of the game class. There's no need to use it.

>    "When a significant process or transformation in the domain is not a natural responsibility of an ENTITY or VALUE OBJECT, add an operation to the model as standalone interface declared as a SERVICE. Define the interface in terms of the language of the model and make sure the operation name is part of the UBIQUITOUS LANGUAGE. Make the SERVICE stateless."
>
>   (Eric Evans Domain-Driven Design)

Furthermore, I suggest you to read the article about [Anemic domain model](https://martinfowler.com/bliki/AnemicDomainModel.html) from Martin Fowler.

 >   "If all your logic is in services, you've robbed yourself blind". 
 >
 > (Martin Fowler)


### - Why do you organized the `packages` that way?

There's no rule for packaging. You can have:

-  Package per feature - Example: The JDK itself (java.util, java.math, java.logging, java.net) - Where interfaces, exceptions and implementations live together.

- Package per layer - Where each application layer resides in a different package.

- Package per type - This is the least used approach. It groups all different kinds of objects (Interfaces, Exceptions, Implementations, etc) in different packages.

The domain project user the `package per feature`. For more information read the following article: [Package by feature, not layer](http://www.javapractices.com/topic/TopicAction.do?Id=205)

### - I have more questions.
Please, contact me ;)