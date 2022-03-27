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

The game domain is a [separated project](./backends/game-domain/) that allows both backends to use the same game logic implemented with pure OOP.

 The only external dependency in the game domain is the usage of the annotation [@JsonbTransient](https://javaee.github.io/javaee-spec/javadocs/javax/json/bind/annotation/JsonbTransient.html) to avoid the `internal board` from the [Game](./backends/game-domain/src/main/java/com/rafabene/mancala/domain/Game.java) class to be serialized to JSON.

 From Eric Evans's [excellent book Domain Driven Design](https://www.amazon.com/gp/product/0321125215/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0321125215&linkCode=as2&tag=martinfowlerc-20):

 >   "Domain Layer (or Model Layer): Responsible for representing concepts of the business, information about the business situation, and business rules. State that reflects the business situation is controlled and used here, even though the technical details of storing it are delegated to the infrastructure. This layer is the heart of business software."


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

#### Defining a custom bakcend URL

You can define a custom backend URL using the Environment Variable `BACKEND_URL`.

    export BACKEND_URL="host:port"
   

## Game instructions

Player 1: Open your browser at <http://localhost:3000/>.

Player 2: Open another tab (or browser) at <http://localhost:3000/> in the same computer.

Once that both players are connected. Hit the `Start Game` button.


## Improvements needed:

 - A `Game pool` can be added to allow more Games and players.

 - [Refactoring](https://refactoring.com/) is always welcomed.

## FAQ

### - Why do you use `Websockets`?
Because it's a game, and the state of the Game (and its pieces like Players, Board, etc) should be immediatly reflected for all players (and even viewers).

The web UI (no matter what presentation technology it uses), will always receive from the websocket, a JSON representation of the game state.

### - Could you use `REST`?
Yes, but this is a web Game, not an API to a Game. This disctinction needs to be very clear. 

### - This `architeture` is different of what I use.

This architeture uses concepts of [DDD - Domain Drive design](https://www.dddcommunity.org/), where the *Domain* is the *heart of the software*. 

The domain lives in its [Bounded Context](https://martinfowler.com/bliki/BoundedContext.html). It's independent from other contexts and technologies. 

Any other application layer that uses it, can expose the game behaviour with a thin layer (as showed with two different backends). 

You can interact with this domain using REST, Websocket, TCP, Assynchronous messaging, etc; without changing a line of code to adapt the game to different applications.

The game domain is independently testable and tested.

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

The *heart* of the software is in the [Game domain](#11-game-domain)

### - Why do you organized the `packages` that way?

There's no rule for packaging. You can have:

- *Package per feature* - Example: The JDK itself (java.util, java.math, java.logging, java.net) - Where interfaces, exceptions and implementations live together.

- *Package per layer* - Where each application layer resides in a different package.

- *Package per type* - This is the least used approach. It groups all different kinds of objects (Interfaces, Exceptions, Implementations, etc) in different packages.

The domain project uses the `package per feature`. For more information, read the following article: [Package by feature, not layer](http://www.javapractices.com/topic/TopicAction.do?Id=205)

### - What if I need to add `more features`?

This is a [MVP](https://en.wikipedia.org/wiki/Minimum_viable_product) -  a version of a product with just enough features to be usable by early customers who can then provide feedback for future product development.

There are some principles that supports a MVP:

 - [YAGNI](https://martinfowler.com/bliki/Yagni.html) - You Aren't Gonna Need It
 
 and
 
 - [Lean Software Development](https://en.wikipedia.org/wiki/Lean_software_development)

 However, if any additional feature is needed, it can be added when you need it.

### - Why don't you have `repositories`?

Because the requirement does not ask for any kind of persistence. There's no reason to implement persistence if it is not needed at this moment.

Check the question about [additional features](#-what-if-i-need-to-add-more-features) above.

### - What are the `design patterns` used?

> "A design pattern is a general repeatable solution to a commonly occurring `problem` in software design."

A `Command` pattern could be used in the Websocket server to encapsulate the user commands as an object.

The websocket server could aso implement the `Observer pattern` if  there were other kind of objects listening for game changes. At this moment only `Websocket sessions` need to be notified. That's why the `Observer pattern` hasn't been used.

No other `Behavioral pattern` seems to be needed.

At this moment, no `Creational pattern` nor `Structural pattern` have been used.

### - And what about the [Java EE](http://www.corej2eepatterns.com/) `design patterns`? 

- The [Game](./backends/game-domain/src/main/java/com/rafabene/mancala/domain/Game.java) class is a `Bussiness Object`. Please, read the definition of a [Business Object here](http://www.corej2eepatterns.com/BusinessObject.htm).
- The `WebsocketOutput` and `WebsocketInput` are  [DTO](http://www.corej2eepatterns.com/TransferObject.htm)s  that transfer data elements to/from the `Application Tier` to the `View` tier.

### - What if I need to `improve the code`?

This is very welcomed!

Software develpment is made through several iterations. Practices like [Refactoring](https://refactoring.com/) are useful to allow `"restructuring an existing body of code, altering its internal structure without changing its external behavior"`.

> "Refactoring is your friend." (Rafael Benevides)

### - Does the game work?

Absolutely! Try it!

### - I have more questions.
Please, contact me. I don't "byte"! ;)