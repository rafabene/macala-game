window.onload = init
let socket = new WebSocket("ws://localhost:8080/game")
let gameData
socket.onmessage = onMessage
socket.onclose = serverDisconnected
socket.onerror = serverDisconnected

function onMessage(event) {
    console.log(event.data)
    updateGameScreen(event.data)
}

function showMessage(message) {
    let messageDiv = document.getElementById("message")
    messageDiv.innerHTML = message
}

function isGameRunning() {
    return gameData.game.gameStatus === "RUNNING";
}

function serverDisconnected(event) {
    showMessage("Server Disconnected. Refresh this page later.")
}

function isPlayerTurn(player) {
    return player.gamerId === gameData.game.playerTurn.gamerId
}

function getUserButtons() {
    let myPits = document.getElementById("myPits")
    let buttons = myPits.getElementsByTagName("button")
    return buttons;
}

function updateGameScreen(gameInput) {
    gameData = JSON.parse(gameInput)
    // Update Gamers
    let player1Div = document.getElementById("player1")
    let player1String = getPlayerString(gameData.game.players[0], gameData.currentPlayer)
    player1Div.innerHTML = player1String

    let player2Div = document.getElementById("player2")
    let player2String = getPlayerString(gameData.game.players[1], gameData.currentPlayer)
    player2Div.innerHTML = player2String

    //Show messages
    showMessage(gameData.message)

    // Show Game Status
    let gameStatusDiv = document.getElementById("gameStatus")
    gameStatusDiv.innerHTML = gameData.game.gameStatus;

    // Opponent Board
    let othermancala = document.getElementById("otherMancala")
    othermancala.innerHTML = gameData.opponentPlayerMancala

    let otherPits = document.getElementById("othePits").children
    let count = 5;
    for (let td of otherPits) {
        if (td.id !== "otherMancala") {
            td.innerHTML = gameData.opponentPlayerPits[count--]
        }
    }

    // Current Player Board
    let myPits = document.getElementById("myPits").children
    count = 0;
    for (let td of myPits) {
       td.children[0].innerHTML = gameData.currentPlayerPits[count++]
    }
    let mymancala = document.getElementById("myMancala")
    mymancala.innerHTML = gameData.currentPlayerMancala

    //Buttons control
    let startGameButton = document.getElementById("startGame")

    if (isGameRunning()) {
        startGameButton.disabled = true;

        // Control Player buttons
        let playerTurn = isPlayerTurn(gameData.currentPlayer);
        
        let myButtons = getUserButtons()
        for (let button of myButtons) {
            if (playerTurn) {
                button.disabled = false;
            } else {
                button.disabled = true;
            }
        }
    } else {
        startGameButton.disabled = false;
    }

}


function getPlayerString(player, currentPlayer) {
    if (player === null) {
        return ''
    } else {
        let playerDetail = ""
        let playerTurn = ""
        // currentPlayer is null for viewers
        if (currentPlayer !== undefined) {
            // We need to understand who is who
            if (player.gamerId === currentPlayer.gamerId) {
                playerDetail = " - (YOU)"
                if (isGameRunning()) {
                    if (isPlayerTurn(player)) {
                        playerTurn = " - YOUR TURN!!!"
                    } else {
                        playerTurn = " - WAIT. It's other player's turn"
                    }
                }
            } else {
                playerDetail = " - (OPPONENT)"
            }
        }
        let id = player.gamerId
        let gamesLoose = player.gamesLoose
        let gamesPlayed = player.gamesPlayed
        let gamesWon = player.gamesWon
        return `Player: ${id} {Won: ${gamesWon} | Loose: ${gamesLoose} | Matches: ${gamesPlayed}} ${playerDetail} <span id="yourturn">${playerTurn}</span>`
    }
}



function init() {
    console.log('Connected')
    let myButtons = getUserButtons()
    for (let button of myButtons) {
        button.onclick = sendCommand("SEED")
    }
    let startGameButton = document.getElementById("startGame")
    startGameButton.onclick = sendCommand("START_GAME")

    let resetGameButton = document.getElementById("resetGame")
    resetGameButton.onclick = sendCommand("RESET_GAME")

}



function sendCommand(command, parameter) {
    let input = {
        operation: command,
    }
    return function (evt) {
        input.parameter = evt.target.id
        console.log(input)
        socket.send(JSON.stringify(input))
    }
}

function handleBoardClick(evt) {
    let pitPosition = evt.target.id
    console.log(pitPosition)
    let input = {
        operation: "SEED",
        parameter: pitPosition
    }
    socket.send(JSON.stringify(input))
}