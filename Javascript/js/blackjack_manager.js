// pcm 20172018a Blackjack oop

let game = null;

function debug(an_object) {
    document.getElementById("debug").innerHTML = JSON.stringify(an_object);
}

function buttons_initialization(){
    document.getElementById("card").disabled     = false;
    document.getElementById("stand").disabled     = false;
    document.getElementById("new_game").disabled = true;
}

function finalize_buttons(){
    document.getElementById("card").disabled     = true;
    document.getElementById("stand").disabled     = true;
    document.getElementById("new_game").disabled = false;
}


//FUNÇÕES QUE DEVEM SER IMPLEMENTADOS PELOS ALUNOS
function new_game(){
    game = new BlackJack();

    //este document... e para fazer reset
    document.getElementById("resultado").innerHTML = "";
    reset_images();

    dealer_new_card();
    dealer_new_card();
    
    player_new_card();

    buttons_initialization();
    //debug( game ) ;
}

function update_dealer(state){
    let string = "";
    for(let i = 0; i < game.get_dealer_cards().length; i++){
        string = string + game.get_dealer_cards()[i];
    }
    if(game.state.gameEnded){
        if( !game.state.dealerWon && !game.state.playerBusted){ //se o jogador ganhar
            document.getElementById("resultado").innerHTML = "Player WON!";
            finalize_buttons();
        }
        else if( game.state.dealerWon && !game.state.playerBusted){
            document.getElementById("resultado").innerHTML = "Dealer WON!";
            finalize_buttons();
        }

    }
    show_dealer_cards();
    //document.getElementById("dealer").innerHTML = JSON.stringify(string);

}

function update_player(state){
    //se quiser mudar a interface graficao, mexo aqui

    let string = "";
    for(let i = 0; i < game.get_player_cards().length; i++){
        string = string + game.get_player_cards()[i];
    }
    
    if(game.state.gameEnded){
        if( !game.state.dealerWon && !game.state.playerBusted){ //se o jogador ganhar
            document.getElementById("resultado").innerHTML = "Player WON!";
            finalize_buttons();
        }

        else if( !game.state.dealerWon && game.state.playerBusted){
            document.getElementById("resultado").innerHTML = "Player Busted !";
            finalize_buttons();
        }
        else if( game.state.dealerWon && game.state.playerBusted){
            document.getElementById("resultado").innerHTML = "Dealer WON!";
            finalize_buttons();
        }
    }
    show_player_cards();
    //document.getElementById("player").innerHTML = JSON.stringify(string);

    //debug(string);


}

function dealer_new_card(){
    let state = game.dealer_move(); //vou sacar a primeira carta do array usando o dealer.move do backjackobject 
    update_dealer(state);
    return state;
}

function player_new_card(){
    let state = game.player_move();
    update_player(state);
    return state;

}

function dealer_finish(){
    //chamar um meteodo que poe true
    game.setDealerTurn(true);

    let state = game.get_game_state();

    update_dealer(state);

    //debug(game);

    while(!game.state.gameEnded){
        dealer_new_card();
        game.get_game_state();
    }

}


function reset_images() {

    let dealer = document.getElementById("dealer1");
    while(dealer.firstChild){
        dealer.removeChild(dealer.firstChild);
    }

    let player = document.getElementById("player");
    while(player.firstChild){
        player.removeChild(player.firstChild);
    }

}


function show_player_cards() {
    let cards = game.get_player_cards();

    for(let i = 0; i < cards.length; i++){
        let image = document.createElement("img");
        image.setAttribute("src", "imgs/cards/" + cards[i] + ".png");
        image.setAttribute("height", "150");
        image.setAttribute("width", "100");
        if(document.getElementById("player").childElementCount <= i) {
            document.getElementById("player").appendChild(image);
        }
    }
}




function show_dealer_cards() {
    let cards = game.get_dealer_cards();

    for(let i = 0; i < cards.length; i++){
        let image = document.createElement("img");
        image.setAttribute("src", "imgs/cards/" + cards[i] + ".png");
        image.setAttribute("height", "150");
        image.setAttribute("width", "100");
        if(document.getElementById("dealer1").childElementCount <= i) {
            document.getElementById("dealer1").appendChild(image);
        }
    }
}