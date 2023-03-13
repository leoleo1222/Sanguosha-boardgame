interface Card {
    val name: String
    var suit: String
    var number: Int
    fun effect(user: Character, target: Character? = null) {}
}

class AttackCard(override var name: String = "Attack \uD83D\uDDE1️") : Card {
    override var suit: String = ""
    override var number: Int = -1
    override fun effect(user: Character, target: Character?) {
//        pile.add(this)
    }
}

class DodgeCard(override var name: String = "Dodge \uD83C\uDFC3\u200D♀️") : Card {
    override var suit: String = ""
    override var number: Int = -1
    override fun effect(user: Character, target: Character?) {
        if (user.hands.contains(this)) {
            user.hands.remove(this)
//            pile.add(this)
        } else {
            user.hp--
        }
    }
}

class PeachCard(override val name: String = "Peach \uD83C\uDF51") : Card {
    override var suit: String = ""
    override var number: Int = -1
    override fun effect(user: Character, target: Character?) {
        if (target == user)
            println("${user.name} used a Peach card to himself/herself.")
        else
            println("<${user.name}> used a Peach card to heal <${target!!.name}>.\")")
        user.heal(1)
    }
}

interface TacticsCard : Card {
    val tacticType: String
        get() = "Tactics"
}

class BarbariansAssault : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
        override val name: String = "Barbarian's Assault \uD83E\uDE96"
    override fun effect(user: Character, target: Character?) {
        for (hero in character) {
            if (hero == user || hero.isDead) continue

            if (hero.alive()) {
                if (hero.useAttackCard()) {
                    // Not so good as that's another effect can use 'Attack' card -> Miss the case
                    val attackCards = hero.hands.filterIsInstance<AttackCard>()
                    val attackCard = hero.chooseCard(hero, attackCards)
                    hero.hands.remove(attackCard)
                    println("\t⚔⚔ ${hero.name} used an Attack card for Barbarians Assault.")
                } else {
                    print("\t⚔⚔ ${hero.name} took 1 damage due to Barbarians Assault.")
                    hero.takeDamage(user, this, 1)
                }
            }
        }
    }
}

class HailofArrows : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Hail of Arrows \uD83C\uDFAF"
    override fun effect(user: Character, target: Character?) {
        for (hero in character) {
            if (hero == user || hero.isDead) continue
            if (hero.alive() && hero.useDodgeCard(hero)) {
                println("\t➵➵ ${hero.name} used a Dodge card due to Hail of Arrows. (${hero.name}'s hand: ${hero.hands - 1} -> ${hero.hands})")
            } else {
                println("\t➵➵ ${hero.name} took 1 damage due to Hail of Arrows.")
                hero.takeDamage(user, this, 1)
            }
        }
    }
}

class OathofPeachGarden : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Oath of Peach Garden \uD83C\uDF51"
    override fun effect(user: Character, target: Character?) {
        println("\t${name}: All hero gain 1 Health due to Peach Garden .")
        for (hero in character) {
            hero.heal(1)
        }
    }
}

class Harvest : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Harvest \uD83C\uDF3E"

    override fun effect(user: Character, target: Character?) {
        if (target != null) {
            val firstCard = library.deck.first()
            target.hands.add(firstCard)
            library.deck.remove(firstCard)
            println("\tRevealed the card [${firstCard.name}] from the deck and gave it to ${target.name} by Harvest \uD83C\uDF3E. ( Hands: ${target.hands.size - 1} -> ${target.hands.size} )")
        }
    }
}

class SleightofHand : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Sleight of Hand \uD83E\uDD1A"
    override fun effect(user: Character, target: Character?) {
        println("${user.name} drew 2 cards with 'Sleight of Hand'.")
        user.drawTwoCards()
//        println("${user.name} has ${user.hands.size} cards in hand. (from ${user.hands.size - 2})")
    }
}

class ImpeccablePlan : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Impeccable Plan \uD83D\uDE45"
    override fun effect(user: Character, target: Character?) {}
}

class BurnBridges : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Burn Bridges \uD83D\uDD25"

    override fun effect(user: Character, target: Character?) {
        if (target != null) {
            println("${user.name} used Burn Bridges \uD83D\uDD25 on ${target.name}")
            // println("Before Hands: ${target.hands.joinToString(", ")}")
            // println("Before Equipment: ${target.equipmentList.joinToString(", ")}")
            val targetStateList = target.stateList + target.equipmentList.filterNotNull()
            val targetAllCards = targetStateList + target.hands
            if (target is HuangYueYing && target.lockEquipment) {
                println("Sorry Huang Yue Ying already used tactics card \uD83D\uDD12, so you can't remove her armor and treasure cards")
            } else {
                if (targetAllCards.isNotEmpty()) {
                    // Discard target equipment
                    val equipmentCards = targetAllCards.filterIsInstance<EquipmentCard>()
                    if (equipmentCards.isNotEmpty()) {
                        val cardToRemove = equipmentCards.last()
                        cardToRemove.removeEquipment(target)
                        println("${user.name} used Burn Bridges \uD83D\uDD25 to discard ${cardToRemove.name} from ${target.name}'s")
                        // println("After Hands: ${target.hands.joinToString(", ")}")
                        // println("After Equipment: ${target.equipmentList.joinToString(", ")}")
                        return
                    }
                    // Discard target delay tactics card
                    val delayTacticsCards = targetAllCards.filterIsInstance<DelayTacticsCard>()
                    if (delayTacticsCards.isNotEmpty()) {
                        target.stateList.remove(delayTacticsCards.last())
                        println("${user.name} used Burn Bridges \uD83D\uDD25 to discard ${delayTacticsCards.last().name} from ${target.name}'s state list")
                        return
                    }
                    // Discard a card from target hero's hand
                    val targetHandCards = target.hands.toMutableList()
                    if (targetHandCards.isNotEmpty()) {
                        val discardedCard = targetHandCards.removeLast()
                        target.hands = targetHandCards
                        println("${user.name} used Burn Bridges \uD83D\uDD25 to discard ${discardedCard.name} from ${target.name}'s hand")
                        // println("After Hands: ${target.hands.joinToString(", ")}")
                        // println("After Equipment: ${target.equipmentList.joinToString(", ")}")
                        return
                    }
                }
            }
        }
    }
}

class Duress : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Duress \uD83D\uDD12"

    override fun effect(user: Character, target: Character?) {
        if (target != null) {
            if (user.useAttackCard()) {
                println("${user.name} used an Attack card on ${target.name} by Duress.")
                user.haveAttacked = false
                user.attack(target)
            } else {
                println("${target.name} has no Attack card to attack.")
                val weapon = target.equipmentList[0] as? WeaponCard
                if (weapon == null) {
                    println("${target.name} has no weapon to be taken.")
                    return
                }
                user.hands.add(weapon)
                println("${user.name} took ${weapon.name} from ${target.name}")
                target.equipmentList[0] = null
                println("${target.name} lost ${weapon.name}")
                // Print out the equipments of equipment list of the target by joining the names of the equipments with ", "
                debug("Equipment list of ${target.name}: ")
                for (equipment in target.equipmentList) {
                    if (equipment != null) print("[${equipment.name}]") else print("[ / ]")
                }
            }
        }
    }

}

class Duel : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Duel \uD83E\uDD4A"

    override fun effect(user: Character, target: Character?) {
        println("\n\uD83E\uDD4A Duel between ${user.name} and ${target?.name} \uD83E\uDD4A")
        // Create a variable to store the player who is responsible to use 'Attack' in the Duel

        if (target != null) {
            var duelTurnPlayer = target
            var duelEnd = false
            var turnCnt = 0

            while (!duelEnd) {
                if (duelTurnPlayer!!.hands.contains(AttackCard())) {
                    duelTurnPlayer.hands.remove(AttackCard())
                    println("\t${duelTurnPlayer.name} discards an AttackCard.")
                } else {
                    println("\t${duelTurnPlayer.name} does not have an AttackCard.")
                    user.takeDamage(duelTurnPlayer, this, 1)
                    duelEnd = true
                }

                turnCnt++
                duelTurnPlayer = if (duelTurnPlayer == user) target else user
                if (turnCnt == 1 && duelTurnPlayer is LuBu) {
                    println(
                        "Lu Bu activates || Matchless \uD83D\uDCAA ||: If Lu Bu are involved in a duel, the other\n" +
                                "player needs to use 2 “Attack” cards."
                    )
                    duelTurnPlayer = if (duelTurnPlayer == user) target else user
                    turnCnt = 0
                }
            }
        }

    }
}

class Plunder : TacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Plunder \uD83D\uDCB0"
    override fun effect(user: Character, target: Character?) {
        println("${user.name} uses Plunder on ${target?.name}")
        if (target is HuangYueYing && target.lockEquipment) {
            println("Sorry Huang Yue Ying already used tactics card, so you can't remove her armor and treasure cards")
        } else {
            if (target != null && user.getClosestEnemyDistance() == 1) {
                val targetCards = target.hands + target.equipmentList
                if (targetCards.isNotEmpty()) {
                    val randomCard = targetCards.random()
                    if (randomCard is Card) {
                        println("${user.name} uses Plunder on ${target.name} and steals ${randomCard.name}")
                        user.hands.add(randomCard)
                        if (randomCard in target.hands) {
                            target.hands.remove(randomCard)
                            println("${target.name} discards ${randomCard.name} from their hand due to Plunder")
                        } else if (randomCard in target.equipmentList) {
                            (randomCard as EquipmentCard).removeEquipment(target)
                            println("${target.name} discards ${randomCard.name} from their equipment list")
                        }
                    }
                }
            } else {
                println("The closest enemy is not in range. (${user.getClosestEnemyDistance()})")
            }
        }
    }
}

interface DelayTacticsCard : Card {
    fun check(target: Character)
}

class Acedia : DelayTacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Acedia \uD83D\uDEAB"
    override fun effect(user: Character, target: Character?) {
        target?.dTacticsZone?.add(this)
    }

    override fun check(target: Character) {
        val checkCard = deck.removeFirst()

        println("\t<< Judgement: Acedia \uD83D\uDE2A >>\n\tJudgement card: \"${checkCard.name}\". Suit: \"${checkCard.suit}\".")
        // check if the card's suit is "Hearts", and SimaYi exists in the game
        // then SimaYi can use one of his hand which is not "Hearts" to replace the checkCard
        if (checkCard.suit == "Hearts" && character.any { it.name == "Sima Yi" }) {
            val simaYi = character.first { it.name == "Sima Yi" }
            if (simaYi != target) {
                val handWithoutHearts = simaYi.hands.filter { it.suit != "Hearts" }
                if (handWithoutHearts.isNotEmpty()) {
                    val chosenCard = handWithoutHearts.random()
                    simaYi.hands.remove(chosenCard)
                    println("\t\t---> Sima Yi replaced the card with \"${chosenCard.name}\" from his hand. Suit: '${chosenCard.suit}'; Rank: '${chosenCard.number}'.")
                }
            }
        } else if (checkCard.suit != "Hearts") {
            println("\t\t---> Activation: ${target.name} skips his main phase.")
            if ((character.any { it.name == "Lu Xun" })) {
                println("Put all his hands onto his hero card. ( Hand: ${target.hands.size} -> 0 )")
            }
            target.isAbandon = true

            (if (target is GuoJia) target.hands else pile).add(checkCard)
            if (target is GuoJia) {
                println("|| Heaven’s Envy ||: Whenever your judgment is resolved, you may put that judgment card into your hand.")
                println("${target.name} put the ${checkCard.name} into his hand.")
            }

        } else {
            println("\t---> Failed: Remove the 'Acecia' to the discard pile.")
        }

        val dCard = target.dTacticsZone.removeFirst()
        pile.add(dCard)

    }

}

class LightningBolt : DelayTacticsCard {
    override var suit: String = ""
    override var number: Int = -1
    override val name: String = "Lightning Bolt ⚠️"
    override fun effect(user: Character, target: Character?) {
        target?.dTacticsZone?.add(LightningBolt())
    }

    override fun check(target: Character) {
        var checkCard = deck.removeFirst()
        pile.add(checkCard)
        println("\t<< Judgement: Lightning Bolt  >>\n\tJudgement card is '${checkCard.name}'. Suit: '${checkCard.suit}'; Rank: '${checkCard.number}'.")

        // check if the card's suit is not "Spades" and checkCard.number >= 2 && checkCard.number <= 9, and SimaYi exists in the game
        // then SimaYi can use one of his hand which is "Spades" and checkCard.number >= 2 && checkCard.number <= 9 to replace the checkCard
        if (checkCard.suit != "Spades" && checkCard.number >= 2 && checkCard.number <= 9 && character.any { it.name == "Sima Yi" }) {
            val simaYi = character.first { it.name == "Sima Yi" }
            if (simaYi != target) {
                val handWithSpades = simaYi.hands.filter { it.suit == "Spades" && it.number >= 2 && it.number <= 9 }
                if (handWithSpades.isNotEmpty()) {
                    val chosenCard = handWithSpades.random()
                    simaYi.hands.remove(chosenCard)
                    checkCard = chosenCard
                    println("\t\t---> Sima Yi replaced the card with \"${chosenCard.name}\" from his hand . Suit: '${chosenCard.suit}'; Rank: '${chosenCard.number}'.")
                }
            }
        }

        // If the above condition is not satisfied, check if the card's suit is "Spades"
        // and its rank is between 2 and 9 (inclusive). If it is, activate the effect of the Lightning Bolt card.
        if (checkCard.suit == "Spades" && checkCard.number >= 2 && checkCard.number <= 9) {
            println("\t\t---> Activation: Thunder has been activated!")
            if ((character.any { it.name == "Lu Xun" })) {
                println("Put all his hands onto his hero card. ( Hand: ${target.hands.size} -> 0 )")
            }
            target.takeDamage(null, this, 3)

        } else {
            // If the card does not satisfy either condition, pass the card to the next player.
            target.dTacticsZone.remove(this)
            val index = character.indexOf(target)
            val nextPlayer = character[(index + 1) % character.size]
            nextPlayer.dTacticsZone.add(this)
            println("\t\t---> Failed: Pass the 'Lightning Bolt' to next player (${nextPlayer.name}). ")
        }
    }

}

interface EquipmentCard : Card {
    val equipmentType: String
    override val name: String
        get() = equipmentType

    fun useEquipment(user: Character) {}
    fun removeEquipment(user: Character) {}
}

abstract class WeaponCard(final override val equipmentType: String = "weapon", val attackRange: Int) : EquipmentCard {
    override fun useEquipment(user: Character) {
        user.attackRange += attackRange
        user.equipmentList[0] = this
    }

    override fun removeEquipment(user: Character) {
        user.attackRange -= attackRange
        user.equipmentList[0] = null
        pile.add(this)
        println(" || Warrior Princess ||: Whenever you remove a weapon, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

class ZhugeCrossbow : WeaponCard(attackRange = 1) {
    override var name = "Zhuge Crossbow \uD83C\uDFF9"
    override var suit: String = ""
    override var number: Int = -1
}

class SwordofBlueSteel : WeaponCard(attackRange = 2) {
    override var name = "Sword of Blue Steel ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class FrostBlade : WeaponCard(attackRange = 2) {
    override var name = "Frost Blade ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class TwinSwords : WeaponCard(attackRange = 2) {
    override var name = "Twin Swords ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class AzureDragonCrescentBlade : WeaponCard(attackRange = 3) {
    override var name = "Azure Dragon Crescent Blade ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class SerpentSpear : WeaponCard(attackRange = 3) {
    override var name = "Serpent Spear ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class RockCleavingAxe : WeaponCard(attackRange = 3) {
    override var name = "Rock Cleaving Axe \uD83E\uDE93"
    override var suit: String = ""
    override var number: Int = -1
}

class HeavenHalberd : WeaponCard(attackRange = 4) {
    override var name = "Heaven Halberd ⚔️"
    override var suit: String = ""
    override var number: Int = -1
}

class KirinBow : WeaponCard(attackRange = 5) {
    override var name = "Kirin Bow \uD83C\uDFF9"
    override var suit: String = ""
    override var number: Int = -1
}

abstract class ArmorCard(final override val equipmentType: String) : EquipmentCard {
    override fun useEquipment(user: Character) {
        user.equipmentList[1] = this
    }

    override fun removeEquipment(user: Character) {
        user.equipmentList[1] = null
        println(" || Warrior Princess \uD83D\uDCB0 ||: Whenever you remove a armor, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

class EightTrigrams : ArmorCard("Armor") {
    override var suit: String = ""
    override var number: Int = -1
    override val name = "Eight Trigrams \uD83D\uDEE1️"

}

interface MountCard : EquipmentCard {
    val mountType: String
    override val name: String
        get() = mountType
    val range: Int

}

class RedHare(override val mountType: String = "Red Hare \uD83D\uDC0E") : MountCard {
    override var suit: String = ""
    override var number: Int = -1
    override val equipmentType: String = "Mount"
    override val name: String = mountType
    override val range: Int = -1

    override fun removeEquipment(user: Character) {
        user.equipmentList[2] = null
        pile.add(this)
        println(" || Sun Shang Xiang ||: Whenever you remove a mount, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

class DaYuan(override val mountType: String = "Da Yuan \uD83D\uDC0E") : MountCard {
    override var suit: String = ""
    override var number: Int = -1
    override val equipmentType: String = "Mount"
    override val name: String = mountType
    override val range: Int = -1

    override fun removeEquipment(user: Character) {
        user.equipmentList[2] = null
        pile.add(this)
        println(" || Sun Shang Xiang ||: Whenever you remove a mount, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

class HuaLiu(override val mountType: String = "Hua Liu \uD83D\uDC0E") : MountCard {
    override var suit: String = ""
    override var number: Int = -1
    override val equipmentType: String = "Mount"
    override val name: String = mountType
    override val range: Int = 1

    override fun removeEquipment(user: Character) {
        user.equipmentList[3] = null
        pile.add(this)
        println(" || Sun Shang Xiang ||: Whenever you remove a mount, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

class TheShadow(override val mountType: String = "The Shadow \uD83D\uDC0E") : MountCard {
    override var suit: String = ""
    override var number: Int = -1
    override val equipmentType: String = "Mount"
    override val name: String = mountType
    override val range: Int = 1

    override fun removeEquipment(user: Character) {
        user.equipmentList[3] = null
        pile.add(this)
        println(" || Warrior Princess ||: Whenever you remove a mount, draw 2 cards.")
        if (user is SunShangXiang) {
            repeat(2) {
                user.drawCard()
            }
        }
    }
}

fun createCard(name: String, suit: String = "Hearts", number: Int = 2): Card {
    return when (name) {
        "Attack" -> AttackCard()
        "Dodge" -> DodgeCard()
        "Peach" -> PeachCard()
        "Barbarians Assault" -> BarbariansAssault()
        "Hail of Arrows" -> HailofArrows()
        "Oath of Peach Garden" -> OathofPeachGarden()
        "Harvest" -> Harvest()
        "Sleight of Hand" -> SleightofHand()
        "Impeccable Plan" -> ImpeccablePlan()
        "Burn Bridges" -> BurnBridges()
        "Duress" -> Duress()
        "Duel" -> Duel()
        "Plunder" -> Plunder()
        "Acedia" -> Acedia()
        "Lightning Bolt" -> LightningBolt()
        "Zhuge Crossbow" -> ZhugeCrossbow()
        "Sword of Blue Steel" -> SwordofBlueSteel()
        "Frost Blade" -> FrostBlade()
        "Twin Swords" -> TwinSwords()
        "Azure Dragon Crescent Blade" -> AzureDragonCrescentBlade()
        "Serpent Spear" -> SerpentSpear()
        "Rock Cleaving Axe" -> RockCleavingAxe()
        "Heaven Halberd" -> HeavenHalberd()
        "Kirin Bow" -> KirinBow()
        "Eight Trigrams" -> EightTrigrams()
        "Red Hare" -> RedHare()
        "Da Yuan" -> DaYuan()
        "Hua Liu" -> HuaLiu()
        "The Shadow" -> TheShadow()
        else -> throw IllegalArgumentException("Invalid card name")
    }.apply {
        this.suit = suit
        this.number = number
    }
}

fun getAllCards(): MutableList<Card> {
    val cardNames = mutableListOf<String>()
    val cardList = mutableListOf<Card>()

    // Add basic cards
    repeat(30) {
        cardList.add(createCard("Attack"))
        cardNames.add("Attack")
    }
    repeat(15) {
        cardList.add(createCard("Dodge"))
        cardNames.add("Dodge")
    }
    repeat(8) {
        cardList.add(createCard("Peach"))
        cardNames.add("Peach")
    }
    repeat(7) {
        cardList.add(createCard("Acedia"))
        cardList.last().suit = getRandomSuit()
        cardList.last().number = getRandomNumber()
        cardNames.add("Acedia")
    }
    repeat(4) {
        cardList.add(createCard("Lightning Bolt"))
        cardList.last().suit = getRandomSuit()
        cardList.last().number = getRandomNumber()
        cardNames.add("Lightning Bolt")
    }

    // Add tactics cards
    val tacticCardNames = listOf(
        "Barbarians Assault",
        "Hail of Arrows",
        "Oath of Peach Garden",
        "Harvest",
        "Sleight of Hand",
        "Impeccable Plan",
        "Burn Bridges",
        "Duress",
        "Duel",
        "Plunder"
    )
    repeat(30) {
        val tacticName = tacticCardNames[it % tacticCardNames.size]
        cardList.add(createCard(tacticName))
        cardList.last().suit = getRandomSuit()
        cardList.last().number = getRandomNumber()
        cardNames.add(tacticName)
    }

    // Add equipment cards
    val weaponCardNames = listOf(
        "Zhuge Crossbow",
        "Sword of Blue Steel",
        "Frost Blade",
        "Twin Swords",
        "Azure Dragon Crescent Blade",
        "Serpent Spear",
        "Rock Cleaving Axe",
        "Heaven Halberd",
        "Kirin Bow"
    )
    val armorCardNames = listOf(
        "Eight Trigrams"
    )
    val mountCardNames = listOf(
        "Red Hare",
        "Da Yuan",
        "Hua Liu",
        "The Shadow"
    )

    val weaponCards = mutableListOf<Card>()
    weaponCardNames.forEach { weaponName ->
        val card = createCard(weaponName)
        card.suit = getRandomSuit()
        card.number = getRandomNumber()
        weaponCards.add(card)
    }

    val armorCard = createCard(armorCardNames[0])
    armorCard.suit = getRandomSuit()
    armorCard.number = getRandomNumber()

    val mountCards = mutableListOf<Card>()
    mountCardNames.forEach { mountName ->
        val card = createCard(mountName)
        card.suit = getRandomSuit()
        card.number = getRandomNumber()
        mountCards.add(card)
    }

    // Add all the cards to the final list
    cardList.addAll(weaponCards)
    cardList.add(armorCard)
    cardList.addAll(mountCards)

    // Shuffle the final list
    cardList.shuffle()

    return cardList
}

fun getRandomSuit(): String {
    val suits = listOf("Spades", "Hearts", "Clubs", "Diamonds")
    return suits.random()
}

fun getRandomNumber(): Int {
    return (1..13).random()
}
