val library: Library = Library()
var deck = library.getdeck()
var pile = library.pile
val character = mutableListOf<Character>()

//val winCondition: Boolean = false;
var winStatement: String = ""
var turnCnt: Int = 1
var debugMode: Boolean = false
fun main() {
    character.add(MonarchFactory.createRandomHero())
    val factory = NonMonarchFactory
    for (i in 0..3) {
        character.add(factory.createRandomHero())
    }

    println("==================================================")
    println("                   Game Start!")
    println("--------------------------------------------------\n")
    println("The deck size: ${deck.size}")

//    for(card in deck){
//        debug("Card: ${card.name}, Suit: ${card.suit}, Rank: ${card.number}")
//    }

    for (i in character) {
        i.initPhase()
    }

    while (character[0].checkWin()) {
        val x = character[0]
        if (!x.isDead) {
            x.templateMethod()
        }
        character.add(character.removeAt(0))
    }

    typewriterText(
        ("\n\n\n==============================================================================================================\n" +
                "                                                   Game Over!" +
                "\n--------------------------------------------------------------------------------------------------------------\n" +
                winStatement +
                "\n==============================================================================================================\n")
    )
}


fun typewriterText(text: String) {
    for (i in text.indices) {
        print(text[i])
        Thread.sleep(7) // Delay between letters
    }
    println()
}

fun debug(text: String) {
    if (debugMode)
        println("Debug: $text")
}
