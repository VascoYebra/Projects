// pcm 20172018a Blackjack object

//constante com o número máximo de pontos para blackJack
const MAX_POINTS = 21;
const naipes = ["C", "D", "E", "P"];


// Classe BlackJack - construtor
class BlackJack {
    constructor() {
        // array com as cartas do dealer
        this.dealer_cards = [];
        // array com as cartas do player
        this.player_cards = [];
        // variável booleana que indica a vez do dealer jogar até ao fim
        this.dealerTurn = false;

        // objeto na forma literal com o estado do jogo
        this.state = {
            'gameEnded': false,
            'dealerWon': false,
            'playerBusted': false
        };

        //métodos utilizados no construtor (DEVEM SER IMPLEMENTADOS PELOS ALUNOS)
        this.new_deck = function () {
            const suits = 4;
            const CARD_PER_SUIT = 13;
            let deck = [];

            for( let k = 0; k < 4; k++ ){
                for( let i = 0; i < 13; i++){
                    deck.push(i+1 + " " + naipes[k]);
                }
            }
            return deck;
            /*
            for(let i = 0; i < suits * (CARD_PER_SUIT); i++ ){
                deck[i] = (i % CARD_PER_SUIT);
            }
            return deck;
            */
        };

        this.shuffle = function (deck) {
            let indexes = [];
            let shuffled = [];
            let index = null;
            for(let n = 0; n < deck.length; n++){
                indexes.push(n);    //copiar para o array auxiliar
            }
            for(let n = 0; n < deck.length; n++){
                index = Math.floor(Math.random()*indexes.length);   //gerar o numero aleatorio e guarda-lo em index
                shuffled.push(deck[indexes[index]]);    
                indexes.splice(index, 1);
            }
            return shuffled;
        };

        // baralho de cartas baralhado
        this.deck = this.shuffle(this.new_deck());
        //this.deck = this.new_deck();
    }

    get_cards_number(cards){
        let number_cards = [];
        let card;
        let card_split;
        for(let i = 0; i < cards.length; i++){
            card = cards[i];
            card_split = card.split(" ");
            number_cards.push(parseInt(card_split[0]));

        }

        return number_cards;

    }

    // métodos
    // devolve as cartas do dealer num novo array (splice)
    get_dealer_cards() {
        return this.dealer_cards.slice();
        //este slice, criar uma copia para fora da classe poder mexer a votnade sem alterar o baralho diretamente das cartas
    }

    // devolve as cartas do player num novo array (splice)
    get_player_cards() {
        return this.player_cards.slice();
    }

    // Ativa a variável booleana "dealerTurn"
    setDealerTurn (val) {
        this.dealerTurn = val;
    }

    //MÉTODOS QUE DEVEM SER IMPLEMENTADOS PELOS ALUNOS
    get_cards_value(cards) {
        //primeiro vou retirar os ÁSES do array. porque? porque eles podem ter dois valores
        //esta funcao returna um booleano, caso a carta não seja o ÀS, ele faz a copia para o array noAces
        //isto percorre o array cards
        let noAces = cards.filter( function(card){ return card != 1;} );
        let figtransf = noAces.map( function(c){return c > 10 ? 10 : c;} );
        //este metodo reduce, reduz o array num so numero, value e o valor da soma até ao momento, e value e o valor a ser acrescentado
        let sum = figtransf.reduce( function(sum, value){return sum+=value;},0);
        //criar variavel auxliar para saber quantos ases tenho:
        let numAces = cards.length - noAces.length;
        while( numAces > 0 ){
            if( sum + 11 >MAX_POINTS ){
                return sum+numAces;
            }
            sum += 11;
            numAces -=1;
        }
        return sum + numAces;
    }

    dealer_move() {
        //vou buscar a primeira carta do baralho
        let card = this.deck[0];
        //agora vou retira-la do baralho
        this.deck.splice(0,1);
        //vou agora por a carta card, declarada na primeira linha, 
        //no array de cartas do dealer
        this.dealer_cards.push(card);
        //chamo esta funcao para verificar se ja rebentei o jogo ou se posso continuar
        return this.get_game_state();
    }

    player_move() {
        //vou buscar a primeira carta do baralho
        let card = this.deck[0];
        //agora vou retira-la do baralho
        this.deck.splice(0,1);
        //vou agora por a carta card, declarada na primeira linha, 
        //no array de cartas do player
        this.player_cards.push(card);
        //chamo esta funcao para verificar se ja rebentei o jogo ou se posso continuar
        return this.get_game_state();

    }

    get_game_state() {
        let playerPoints = this.get_cards_value(this.get_cards_number(this.player_cards));
        let dealerPoints = this.get_cards_value(this.get_cards_number(this.dealer_cards));
        let playerBusted = playerPoints > MAX_POINTS;
        //se o jogador ja estiver 21 pontos, o jogador ganha automaticamente
        let playerWon = playerPoints === MAX_POINTS;
        //tenho que garantir que estou no turno do dealer!
        let dealerBusted = this.dealerTurn && (dealerPoints > MAX_POINTS);
        let dealerWon = this.dealerTurn && (dealerPoints <= MAX_POINTS) && (dealerPoints > playerPoints);
        //agora vou dizer quando e que o jogo acaba
        this.state.gameEnded = playerBusted || playerWon || dealerBusted || dealerWon;
        //vou dizer quando e que o dealer ganha
        this.state.dealerWon = dealerWon;
        //finalemnte quando o player ganha
        this.state.playerBusted = playerBusted;
        return this.state;

        /*
        if( this.get_cards_value(this.player_cards) >= 21 ){
            if( this.get_cards_value(this.player_cards) == 21 ){
                return dealerWon;   //dealer perde
            }
            return playerBusted;
        }
        if( this.get_cards_value(this.dealer_cards) >= 21 ){
            if( this.get_cards_value(this.dealer_cards) == 21 ){
                return !dealerWon;  //dealer ganha
            } 
            return !playerBusted;
        }
        */
    }
    
}

