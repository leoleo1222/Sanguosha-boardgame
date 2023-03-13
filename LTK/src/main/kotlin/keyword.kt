import kotlin.math.floor

// Here are some keywords of the characters would use

//1. Play:
//To play a card, you announce the card name and target a hero, its effect will trigger and
//after it resolves, it will enter the discard pile
//fun Character.play(card: Card, target: Character) {
//    // announce the card name and target
//    println("$name plays ${card.name} on ${target.name}")
//
//    // trigger the effect of the card
//    card.effect(this, target)
//
//    // move the card to the discard pile
//    discardPile.add(card)
//
//    // remove the card from the player's hand
//    hands.remove(card)
//
//}

//2. Use:
//Whenever a card or effect asks you to use a card, it means you respond to that effect by
//revealing and discards the card you have “used”. Its effect will not be triggered
//fun Character.use(card: Card) {
//    hands.remove(card)
//    println("$name used ${card.name}")
//}


//4. Give
//To give a card, you pass the card face down to another player, that hero owns that card
//from onwards
//fun Character.give(card: Card, recipient: Character) {
//    // Check if the current character has the card
//    if (hands.contains(card)) {
//        // Remove the card from the current character's hand
//        hands.remove(card)
//        // Give the card to the recipient
//        recipient.addToHand(card)
//        println("$name gave ${recipient.name} a card: ${card.name}")
//    } else {
//        println("$name does not have ${card.name} to give.")
//    }
//}

// Add the given card to the recipient's hand
//fun Character.addToHand(card: Card) {
//    hands.add(card)
//}


//7. Replace
//To replace a card, you use the new card to replace the target card and the target card will
//be discarded
//fun Character.replace(targetCard: Card, newCard: Card) {
//    // Find the target card in hand and replace it with the new card
//    val index = hands.indexOf(targetCard)
//    if (index != -1) {
//        hands[index] = newCard
//        println("$name replaces ${targetCard.name} with ${newCard.name}")
//    }
//    // Discard the target card if it's not found in hand
//    else {
//        discardPile.add(targetCard)
//        println("$name can't replace ${targetCard.name} because it's not in their hand")
//    }
//}

fun Character.templateMethod() {
    println("\n\n--- Turn ${turnCnt++}, $name (${role.roleTitle}) ---")

    judgmentPhase()
    drawPhase()
    mainPhase()
    discardPhase()
    endPhase()
}

// Please note that this phrase would reset variables as well
// e.g. haveAttacked, isAbandon


fun Character.initPhase() {
    drawTwoCards()
    drawTwoCards()
    println("- Draw 4 cards. Current hand: ${hands.size}")
    println("Current Hands: ${hands.joinToString { "[" + it.name + "] " }}")
}

fun Character.mainPhase() {
    if (isDead || !checkWin())
        return

    // Maybe develop strategy pattern to decide...
    println("* Main Phrase")
    if (isAbandon) {
        println("\t${name} is Abandon, skip the main phrase.")
        return
    }

    println("\tHP: $hp")
    println("\tCurrent Hands: ${hands.joinToString { "[" + it.name + "] " }}")
    // Println the equipment name in the equipment list
    print("\tEquipment: ")
    for (equipment in equipmentList) {
        if (equipment != null) print("[${equipment.name}]") else print("[ / ]")
    }
    println()

    while (actionAble() && !isDead) {
    }

}


fun chooseCard(cards: List<Card>): Card {
    return cards.random()
}

fun Character.checkWin(): Boolean {
    val monarch = character.first { it.role is Monarch }
    val minister = character.first { it.role is Minister }
    val traitors = character.first { it.role is Traitors }
    val traitors2 = character.last { it.role is Traitors }
    val rebels = character.first { it.role is Rebel }

    // Check if Monarch is alive, Traitors and Rebels are dead
    if (!monarch.isDead && minister.isDead && traitors.isDead && traitors2.isDead && rebels.isDead) {
        winStatement = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nWinner: \n[Monarch wins]!\n${monarch.title} -- ${monarch.name}"
        return false
    }

    // Check if Monarch and Minister are alive, Traitors and Rebels are dead
    if (!monarch.isDead && !minister.isDead && traitors.isDead && traitors2.isDead && rebels.isDead) {
        winStatement = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nWinners: \n[Monarch and Minister win!]\n" +
                       "${monarch.title} -- ${monarch.name}\n" +
                       "${minister.title} -- ${minister.name}"
        return false
    }

    if (monarch.isDead) {
        winStatement = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nWinners: \n[Traitors win!]\n" +
                       "${traitors.title} -- ${traitors.name}" +
                       "${traitors2.title} -- ${traitors2.name}"
        return false
    }

    if (monarch.isDead && minister.isDead && traitors.isDead && traitors2.isDead && !rebels.isDead) {
        winStatement = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nWinner: \n[Rebel wins!]\n" +
                       "${rebels.title} -- ${rebels.name}"
        return false
    }
    return true
    // If neither condition is met, return false
}

// It returns a boolean and to determine whether a user can do further actions
fun Character.actionAble(): Boolean {
    if (isDead || !checkWin())
        return false

    if(hp < maxHP-1){
        setUnhealthyState()
    }else{
        setHealthyState()
    }

    println("Strategy: ${basicStrategy.recommend}")

    // 1. equip
    if (equip()) {
        print("\tEquipment: ")
        for (equipment in equipmentList) {
            if (equipment != null) print("[${equipment.name}]") else print("[ / ]")
        }
        println()
        return true
    }

    // 2. Update Enemy(?)
    getEnemyDistance()
    for (enemy in enemyDistance) {
        debug("\t- Enemy: ${enemy.key.name} (${enemy.key.roleTitle}), Distance: ${enemy.value}, HP: ${enemy.key.hp}")
        // Print enemy's equipments in equipmentList in the debug mode
//        debug("\tEquipment: ${enemy.key.equipmentList.joinToString { "[" + it?.name + "] " }}")
    }
    debug("\t-> Closest Enemy: ${getClosestEnemy()!!.name}")

    // 3. DelayTactic
    // Use a for loop to check if there is any delay tactic card in the hand
    for (card in hands) {
        if (card is DelayTacticsCard) {
            // If there is a delay tactic card, use it
            placeDelayTactic()
            return true
        }
    }

    // 4. Attack()
    if (!haveAttacked) {
        attack(getClosestEnemy()!!)
    }

    // 5. Heal()
    if (hp < maxHP)
        HealCase()

    // 6. UseTacticCard()
    if (checkWin())
        useTacticCard()

    // 7. useAbility()
    if (checkWin())
        useAbility()

    // we check if the character(this), who is also LuXun (this is LuXun)
    // we will get the allies with: val allies = character.filterNot { enemyDistance.containsKey(it) }.toMutableList()
    // if LuXun lost his last card, then he can give the amount of hp card to his allies
    // 8. giveCardToAlly()
    if (this is LuXun && hands.size == 0) {
        val allies = character.filterNot { enemyDistance.containsKey(it) }.toMutableList()
        if (allies.size > 0) {
            val numCardsToGive = minOf(allies.size, hp)
            for (i in 1..numCardsToGive) {
                val cardToGive = deck.removeFirst()
                allies[i - 1].hands.add(cardToGive)
                println("---> ${allies[i - 1].name} get a card from the deck due to Lu Xun ability: One after another")
            }
        }
    }

    return false
}

fun Character.useTacticCard() {
    if (checkWin())
        return

    val tacticsCardsInHand = hands.filterIsInstance<TacticsCard>()

    val harvestCard = tacticsCardsInHand.find { it is Harvest }
    if (harvestCard != null) {
        hands.remove(harvestCard)
        pile.add(harvestCard)
        println("$name is using Tactic Card ${harvestCard.name} ( Hands:　${hands.size + 1}　-> ${hands.size} )")
        for (target in getAliveCharacter()) {
            harvestCard.effect(this, target)
        }
    } else {
        for (card in getTacticCards()) {
            val matchingCard = tacticsCardsInHand.find { it.name == card.name }

            if (matchingCard != null) {
                println("$name is using Tactic Card ${card.name}")

                if (!useAttackCard() && matchingCard is Duel) {
                    debug("The player has no attack card in hand. Thus, he cannot use Duel.")
                    continue
                }

                val closestEnemy = getClosestEnemy()

                if (closestEnemy != null) {
                    // Check if the closest enemy has the ImpeccablePlan card in their hand
                    val hasImpeccablePlan = closestEnemy.hands.any { it is ImpeccablePlan }
                    if (hasImpeccablePlan) {
                        println("${closestEnemy.name} uses the Impeccable Plan to block the ${matchingCard.name}.")
                        closestEnemy.hands.removeIf { it is ImpeccablePlan }
                        pile.add(ImpeccablePlan())
                        hands.remove(matchingCard)
                        pile.add(matchingCard)
                    } else {
                        if (card is Plunder && getClosestEnemyDistance() != 1) {
                            println("The closest enemy is not in range for Plunder.")
                        } else {
                            card.effect(this, closestEnemy)
                            hands.remove(matchingCard)
                            pile.add(matchingCard)
                        }
                    }
                }
            }
        }
    }

}

fun getTacticCards(): List<TacticsCard> {
    return listOf(
        BarbariansAssault(),
        HailofArrows(),
        OathofPeachGarden(),
        SleightofHand(),
        BurnBridges(),
        Duress(),
        Duel(),
        Plunder()
    )
}

fun Character.equip(): Boolean {
    val equipmentIndex = hands.indexOfFirst { it is EquipmentCard }
    if (equipmentIndex == -1) {
        return false
    }

    val equipment = hands.removeAt(equipmentIndex) as EquipmentCard

    val previousEquipment = when (equipment) {
        is WeaponCard -> {
            val previous = equipmentList[0]
            equipmentList[0] = equipment
            previous
        }
        is ArmorCard -> {
            val previous = equipmentList[1]
            equipmentList[1] = equipment
            previous
        }
        is MountCard -> {
            when (equipment.range) {
                -1 -> {
                    val previous = equipmentList[2]
                    equipmentList[2] = equipment
                    previous
                }
                1 -> {
                    val previous = equipmentList[3]
                    equipmentList[3] = equipment
                    previous
                }
                else -> null
            }
        }
        else -> null
    }

    if (previousEquipment != null) {
        println("The ${previousEquipment.name} is replaced by ${equipment.name} ( Hands: ${hands.size + 1} -> ${hands.size} )")
    }

    println("$name equipped ${equipment.equipmentType}: ${equipment.name} ( Hands: ${hands.size + 1} -> ${hands.size} )")
    return true
}


fun Character.findDistance(target: Character, numPlayers: Int): Int {
    val currIndex = character.indexOf(this)
    val targetIndex = character.indexOf(target)
    val clockwise = (targetIndex - currIndex + numPlayers) % numPlayers
    val counterclockwise = (currIndex - targetIndex + numPlayers) % numPlayers
    val baseRange = minOf(clockwise, counterclockwise)
    // check if there is any mount card in the equipment list, then cast it to MountCard
    if (target.equipmentList[3] != null) {
        val mountCard = target.equipmentList[3] as MountCard
        return baseRange + mountCard.range
    }
//    val mountCard = target.equipmentList[3] as? MountCard
    // Simplify line 279 to 282
    return baseRange
}

// return the closet enemy by min distance in the enemyRange
// if there is no enemy, return null
fun Character.getClosestEnemy(): Character? {
    var minDistance = 100
    var closestEnemy: Character? = null
    // Update the enemyDistance before finding the closest enemy
    getEnemyDistance()
    for (enemy in enemyDistance) {
        if (enemy.value < minDistance) {
            minDistance = enemy.value
            closestEnemy = enemy.key
        }
    }
    return closestEnemy
}

// Create a method getClosestEnemyDistance() to return distance od the closest enemy
fun Character.getClosestEnemyDistance(): Int {
    val cloestEnemy = getClosestEnemy()
    return enemyDistance[cloestEnemy]!!
}

fun Character.placeDelayTactic() {
    // Check if there is any delay tactic in hand
    val delayTacticIndex = hands.indexOfFirst { it is DelayTacticsCard }
    if (delayTacticIndex != -1) {
        val delayTactic = hands.removeAt(delayTacticIndex) as DelayTacticsCard
        val enemy = getClosestEnemy()
        enemy!!.dTacticsZone.add(delayTactic)
        println("$name placed ${delayTactic.name} in Delay Tactics Zone to ${enemy.name} ( Hands: ${hands.size + 1} -> ${hands.size} )")
        println("\t${enemy.name}'s Delay Tactics Zone: ${enemy.dTacticsZone.joinToString { "[" + it.name + "] " }}")
    }
}

fun Character.HealCase() {
    if (hands.any { it is PeachCard || it is OathofPeachGarden } && hp <= floor((maxHP / 2).toDouble())) {
        val healCard = hands.first { it is PeachCard || it is OathofPeachGarden }
        playCard(healCard, this)
    }
}








