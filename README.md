# mancala-game
Implementation of Macala game


You can find some visual explanations of the game rules by running a [Google Search](https://www.google.com/search?q=manca+game) for Mancala or Kalaha game.

This implementation uses to parts to work.

#### 1 - Java Backend

Implemented using the [MicroProfile](https://microprofile.io/) specification and [Helidon](https://helidon.io/) implementation.

#### 2 - HTML Frontend

Implemented using just HTML and Javascript. [NodeJS](https://nodejs.org/en/) has been used just to serve the static content. Later `nodejs` will be used to parameterize the backend address. 

To execute this game you need to execute both parts in the same host.

## Instructions do run the game locally

### Backend: 

Compile and package the game using [Maven](https://maven.apache.org/). 

(This step also runs the unit tests)

    cd backend
    mvn clean package

Execute the backend

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



