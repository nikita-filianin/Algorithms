
cards.init({table:'#card-table', blackJoker: true, redJoker: true});



let deck = new cards.Deck(); 

deck.x -= 50;

deck.addCards(cards.all); 

deck.render({immediate:true});


let computerTop = new cards.Hand({faceUp:false, y:60});
let computerLeft = new cards.Hand({faceUp:false, y:200});
computerLeft.x -= 300;
let computerRight = new cards.Hand({faceUp:false, y:200});
computerRight.x += 300;
let human = new cards.Hand({faceUp:true, y:340});


let gamePile = new cards.Deck({faceUp:true});
gamePile.x += 50;

let players = [
	{ player: human, isHuman: true, name: "Вы", skipTurn: false },
	{ player: computerRight, isHuman: false, name: "Игрок справа", skipTurn: false },
	{ player: computerTop, isHuman: false, name: "Игрок перед", skipTurn: false },
	{ player: computerLeft, isHuman: false, name: "Игрок слева", skipTurn: false }
];

let currentPlayerIdx = 0;
let lastPlayerIdx = 0;
let nCardsPlayed = 0;
let nSkippedTurns = 0;

const computerPlayDelay = 1000;

const TWO = 15;
const ACE = 14;
const JOKER = 16;

function displayCurrentPlayerName()
{
	$('#playerName').text("Ходит: " + players[currentPlayerIdx].name);
}

function getCurrentPlayer() {
	displayCurrentPlayerName();
	return players[currentPlayerIdx].player;
}

function getCurrentPlayerName() {
	return players[currentPlayerIdx].name;
}

function getNextPlayer() {
	if (nSkippedTurns < players.length)
	{
		currentPlayerIdx = (currentPlayerIdx + 1) % players.length;
	}
	else
	{
		closeGame();
		currentPlayerIdx = lastPlayerIdx;
		nSkippedTurns = 0;
	}
	return getCurrentPlayer();
}

function getPreviousPlayer() {
	return players[currentPlayerIdx === 0 ? players.length - 1 : currentPlayerIdx - 1].player;
}

function isCurrentPlayerHuman() {
	return players[currentPlayerIdx].isHuman;
}

function getCardRank(card) {
	let rank = card.rank;

	switch (card.rank) {
		case 1:
			rank = ACE;
			break;

		case 2:
			rank = TWO;
			break;

		case 0:
			rank = JOKER;
			break;
	
		default:
			rank = card.rank;
			break;
	}

	return rank;
}

function compareCards(a, b)
{
	let rankA = getCardRank(a);
	let rankB = getCardRank(b);

	if (rankA > rankB)
	{
		return 1;
	}
	else
	{
		return -1;
	}
}


$('#deal').click(function() {

	$('#deal').hide();
	let playersArray = [];
	players.forEach(p => {
		playersArray.push(p.player);
	});
	deck.deal(cards.all.length / playersArray.length, playersArray, 50, function() {
		playersArray.forEach(player => {
			player.sort(compareCards);
			player.render();
		});
	});

	displayCurrentPlayerName();
});

let selectedCards = [];

function isSelected(aCard)
{
	return selectedCards.indexOf(aCard) >= 0;
}

function selectCard(aCard)
{
	aCard.moveTo(aCard.targetLeft + cards.options.cardSize.width / 2, aCard.targetTop  + cards.options.cardSize.height / 2 - 20);
	selectedCards.push(aCard);
}

function deselectCard(aCard)
{
	aCard.moveTo(aCard.targetLeft + cards.options.cardSize.width / 2, aCard.targetTop + cards.options.cardSize.height / 2);
	selectedCards.splice(selectedCards.indexOf(aCard), 1);
}

human.click(function(card){
	if (!isSelected(card))
	{
		selectCard(card);
	}
	else
	{
		deselectCard(card);
	}
});

function getMaxRank(hand)
{
	let maxRank = 0;
	if (hand.length === 0)
	{
		return maxRank;
	}

	hand.forEach(card => {
		let rank = getCardRank(card);
		if (rank > maxRank)
		{
			maxRank = rank;
		}
	});

	return maxRank;
}

function getRandomCard(hand) {
	if (hand.length === 0)
	{
		return null;
	}
	
	return hand[Math.floor(Math.random() * hand.length)];
}

function closeGame()
{
	while (gamePile.length > 0) {
		deck.addCard(gamePile.topCard());
	}

	deck.render();
	gamePile.render();
}

function getRandomCardGreaterThan(player, minRank)
{
	let randomCard;
	let randomCardRank;
	do {
		randomCard = getRandomCard(player);
		randomCardRank = getCardRank(randomCard);
	} while (randomCardRank < minRank);

	return randomCard;
}

function getComputerCards(player)
{
	let cards = [];
	let gamePileTopCardRank = 0;

	if (gamePile.length > 0)
	{
		gamePileTopCardRank = getCardRank(gamePile.topCard());
	}

	let maxRank = getMaxRank(player);

	if (maxRank < gamePileTopCardRank)
	{
		return cards;
	}

	if (nCardsPlayed === 0)
	{
		nCardsPlayed = 1;
	}

	if (nCardsPlayed === 1)
	{
		let sameRankRequired = false;
		if (gamePile.length > 1)
		{
			// Check rank of card under top one
			sameRankRequired = getCardRank(gamePile[gamePile.length - 2]) == gamePileTopCardRank;
		}

		if (sameRankRequired && !getPreviousPlayer().skipTurn)
		{
			for (let index = 0; index < player.length; index++) {
				const card = player[index];
				if (getCardRank(card) == getCardRank(gamePile[gamePile.length - 2]))
				{
					cards.push(card);
					break;
				}
			}
		}
		else
		{
			let cardTwo = null;
			let cardJoker = null;

			if (gamePile.length > 0)
			{
				for (let cardIdx = 0; cardIdx < player.length; cardIdx++)
				{
					const card = player[cardIdx];
					if (getCardRank(card) === TWO)
					{
						cardTwo = card;
					}
					if (getCardRank(card) === JOKER)
					{
						cardJoker = card;
					}
				}
			}

			if (gamePileTopCardRank > 11 && cardTwo)
				cards.push(cardTwo);
			else if (gamePileTopCardRank > 11 && cardJoker)
				cards.push(cardJoker);
			else
			{
				let randomCard = getRandomCardGreaterThan(player, gamePileTopCardRank);
				cards.push(randomCard);
			}
		}
	}
	else if (nCardsPlayed === 2)
	{
		for (let cardIdx = 0; cardIdx < player.length; cardIdx++)
		{
			const card = player[cardIdx];

			if (getCardRank(card) < gamePileTopCardRank)
			{
				continue;
			}

			const nextCard = cardIdx === player.length - 1 ? null : player[cardIdx + 1];

			if (nextCard && getCardRank(card) === getCardRank(nextCard))
			{
				cards.push(card);
				cards.push(nextCard);
				break;
			}
		}
	}
	else if (nCardsPlayed === 3)
	{
		for (let cardIdx = 0; cardIdx < player.length; cardIdx++)
		{
			const card = player[cardIdx];
			
			if (getCardRank(card) < gamePileTopCardRank)
			{
				continue;
			}

			const nextCard = cardIdx === player.length - 1 ? null : player[cardIdx + 1];
			const nextNextCard = cardIdx === player.length - 2 ? null : player[cardIdx + 2];

			if (nextCard && nextNextCard && getCardRank(card) === getCardRank(nextCard) &&
				getCardRank(card) === getCardRank(nextNextCard))
			{
				cards.push(card);
				cards.push(nextCard);
				cards.push(nextNextCard);
				break;
			}
		}
	}

	return cards;
}

function computer(player)
{
	if (player.length === 0)
		return;

	let computerCards = getComputerCards(player);
	
	if (computerCards.length == 0)
	{
		alert(getCurrentPlayerName() + " пропускает ход!");
		player.skipTurn = true;
		nSkippedTurns++;
		closeGameIfNeeded();
		return;
	}

	player.skipTurn = false;
	nSkippedTurns = 0;
	lastPlayerIdx = currentPlayerIdx;

	nCardsPlayed = computerCards.length;

	computerCards.forEach(card => {
		gamePile.addCard(card);
	});
	
	gamePile.render();
	player.render({
		callback: closeGameIfNeeded
	});
}

function moveGamePileCards()
{
	for (let index = 0; index < nCardsPlayed; index++)
	{
		const aCard = gamePile[gamePile.length - 1 - index];
		aCard.moveTo(aCard.targetLeft + cards.options.cardSize.width / 2 + (20 * index), aCard.targetTop  + cards.options.cardSize.height / 2);
	}
}

function gameMustBeClosed()
{
	if (gamePile.length === 0)
		return false;

	let gamePileTopCardRank = getCardRank(gamePile.topCard());

	if (gamePile.length >= 4 &&
		getCardRank(gamePile[gamePile.length - 2]) == gamePileTopCardRank &&
		getCardRank(gamePile[gamePile.length - 3]) == gamePileTopCardRank &&
		getCardRank(gamePile[gamePile.length - 4]) == gamePileTopCardRank)
	{
		return true;
	}

	if (nCardsPlayed === 1)
	{
		if (gamePileTopCardRank === TWO || gamePileTopCardRank === JOKER)
		{
			return true;
		}	
	}
	else if (nCardsPlayed === 2)
	{
		if ((gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == TWO) ||
			(gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == JOKER) ||
			(gamePileTopCardRank === JOKER && getCardRank(gamePile[gamePile.length - 2]) == TWO) ||
			(gamePileTopCardRank === JOKER && getCardRank(gamePile[gamePile.length - 2]) == JOKER))
		{
			return true;
		}
	}
	else if (nCardsPlayed === 3)
	{
		if ((gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == TWO && getCardRank(gamePile[gamePile.length - 3]) == TWO) ||

			(gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == TWO && getCardRank(gamePile[gamePile.length - 3]) == JOKER) ||
			(gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == JOKER && getCardRank(gamePile[gamePile.length - 3]) == TWO) ||
			(gamePileTopCardRank === JOKER && getCardRank(gamePile[gamePile.length - 2]) == TWO && getCardRank(gamePile[gamePile.length - 3]) == TWO) ||

			(gamePileTopCardRank === TWO && getCardRank(gamePile[gamePile.length - 2]) == JOKER && getCardRank(gamePile[gamePile.length - 3]) == JOKER) ||
			(gamePileTopCardRank === JOKER && getCardRank(gamePile[gamePile.length - 2]) == TWO && getCardRank(gamePile[gamePile.length - 3]) == JOKER) ||
			(gamePileTopCardRank === JOKER && getCardRank(gamePile[gamePile.length - 2]) == JOKER && getCardRank(gamePile[gamePile.length - 3]) == TWO))
		{
			return true;
		}
	}

	return false;
}

function closeGameIfNeeded()
{
	moveGamePileCards();

	if (gameMustBeClosed())
	{
		setTimeout(function() {
			closeGame();
		}, 1000);

		if(!isCurrentPlayerHuman())
		{
			// Same player plays again
			setTimeout(function() {
				computer(getCurrentPlayer());
			}, computerPlayDelay);
		}
	}
	else
	{
		let nextPlayer = getNextPlayer();
		if(!isCurrentPlayerHuman())
		{
			setTimeout(function() {
				computer(nextPlayer);
			}, computerPlayDelay);
		}
	}
}

$('#play').click(function() {
	nCardsPlayed = selectedCards.length;

	if (nCardsPlayed === 0)
		return;

	getCurrentPlayer().skipTurn = false;
	nSkippedTurns = 0;
	lastPlayerIdx = currentPlayerIdx;

	selectedCards.forEach(card => {
		gamePile.addCard(card);
	});

	human.render();
	gamePile.render({
		callback: closeGameIfNeeded
	});

	selectedCards = [];
});

$('#skip').click(function() {
	nSkippedTurns++;
	computer(getNextPlayer());
});
