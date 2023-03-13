class Library {
    var deck: MutableList<Card> = mutableListOf()
    var pile: MutableList<Card> = mutableListOf()

    fun reFill() {
        debug("Pile size: ${pile.size} ***************** $this")
        for (card in pile) {
            deck.add(card)
        }
        deck.shuffle()
        pile.clear()
        debug("Deck.size: ${deck.size} ***************** $this")
    }

    fun getdeck(): MutableList<Card> {
        if (deck.isEmpty()) {
            deck = getAllCards().toMutableList()
            deck.shuffle()
        }
        return deck
    }

    // For the method like 'Harvest' to reveal some cards
    fun revealCards(numCards: Int): List<Card> {
        val revealedCards = mutableListOf<Card>()
        repeat(numCards) {
            if (deck.isNotEmpty()) {
                val card = deck.removeAt(0)
                revealedCards.add(card)
                println("Revealed card: ${card.name}")
            }
        }
        return revealedCards
    }
}

// how to use this class
//val library = Library()
//val allCards = library.getAllCards()
//println(allCards)