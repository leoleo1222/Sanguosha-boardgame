import kotlin.random.Random

abstract class Ability {
    open val name: String = ""
    open val description: String = ""
    open fun use(user: Character, target: Character?) {}
}

class KindnessAbility : Ability() {
    override val name: String = "Kindness"
    override val description: String = "Once per main phase, you may give any number of cards in your hand " +
            "to another hero. Then that hero cannot receive any 'Kindness' cards until the end of the turn.\n" +
            "During your main phase, whenever you give out the second 'Kindness' card, you may treat this as " +
            "playing a basic card"

    override fun use(user: Character, target: Character?) {
        var receivedKindnessCards = 0
        val basicCards = listOf(AttackCard(), DodgeCard(), PeachCard()).shuffled()
        val attackTarget: MutableList<Character> = mutableListOf()
        if (user.hands.size >= 2) {
            attackTarget.clear()
            for (i in 1 until character.size) {
                if (character[i].roleTitle != "Minister") {
                    attackTarget.add(character[i])
                    println("attackTarget: $attackTarget")
                } else {
                    repeat(2) {
                        character[i].hands.add(user.hands.removeAt(Random.nextInt(user.hands.size)))
                        receivedKindnessCards++
                        println("Minister received cards: $receivedKindnessCards")
                    }
                }
            }
        } else {
            println("You don't have enough cards to use this ability.")
        }

        if (receivedKindnessCards == 2) {
            if (basicCards.first() is AttackCard) {
                println("${user.name} used KindnessAbility to attack ${attackTarget.shuffled().first().name}")
                target?.dodge(target)
            } else if (basicCards.first() is DodgeCard) {
                println("${user.name} used KindnessAbility to play a DodgeCard")
                user.hands.add(basicCards.first())
                user.useDodgeCard(user)
            } else {
                PeachCard().effect(user, user)
            }
        }
    }
}

class RouseAbility : Ability() {
    override val name: String = "Rouse"
    override val description: String =
        "Whenever you need to play or use an ‘Attack’, any other Shu heroes can play or use for you"

    override fun use(user: Character, target: Character?) {
        if (user is LiuBei && (user.wantAttack || user.isBarbariansAssault || user.isDuel)) {
            user.attackHero()
        }
    }
}

class SaintWarriorAbility : Ability() {
    override val name: String = "Saint Warrior"
    override val description: String =
        "You may play or use any red cards from your hand or equipment zone as ‘Attack’. Your ♦ “Attack” is not limited by attack range."


    override fun use(user: Character, target: Character?) {
        if (user is GuanYu) {
            if (user.hands.filterIsInstance<AttackCard>().isEmpty()) {
                println("Change before : ${user.hands.first().suit} *************")
                user.hands.first() { it.suit == "Hearts" || it.suit == "Diamonds" } as AttackCard
                println("Change after : ${user.hands.first().suit} *************")
                if (target != null) {
                    user.attack(target)
                }
            }
        }
    }
}

class RoarAbility : Ability() {
    override val name: String = "Roar"
    override val description: String =
        "You may play any number of 'Attack' during your turn. As long as you have played an 'Attack' during your turn, 'Attack' you play during your turn is not limited by attack range."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class StargazingAbility : Ability() {
    override val name: String = "Stargazing"
    override val description: String =
        "At the beginning of the turn, you may look at the top 5 cards of your library, (When surviving heroes are 3 or less, top 3 cards instead of 5), you may choose to put any number of these cards on top of the library and bottom of the library in any order. If you put all cards to the bottom of the library, you may use 'Stargazing' again at the end of turn."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class EmptyFortAbility : Ability() {
    override val name: String = "Empty Fort"
    override val description: String =
        "(Passive) When you have no cards in your hand, you cannot be the target of 'Attack' or 'Duel'"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class DragonCourageAbility : Ability() {
    override val name: String = "Dragon Courage"
    override val description: String = "You may play or use 'Attack' as 'Dodge' and vice versa."
    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class HorsemanshipAbility : Ability() {
    override val name: String = "Horsemanship"
    override val description: String = "Your range with other heroes is always reduced by 1."
    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class IronCalvaryAbility : Ability() {
    override val name: String = "Iron Calvary"
    override val description: String =
        "Whenever you play an 'Attack' targeting a hero, you may perform a judgment and have that hero lose all non-passive abilities until the end of the turn. Unless that hero discard a card with the same suit as the judgment card, the 'Attack' cannot be responded by 'Dodge'."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class GatheringWisdomAbility : Ability() {
    override val name: String = "Gathering Wisdom"
    override val description: String =
        "Whenever you play a non-delay tactics card, you may draw a card. If this card is a basic card, you may discard this card and this turn your maximum hand size is increased by 1."

    override fun use(user: Character, target: Character?) {
        val drawCard = deck.removeFirst()
        user.hands.add(drawCard)
        println("|| Gathering Wisdom ||: Whenever you play a non-delay tactics card, you may draw a card.")
        println("${user.name} draw a card: ${drawCard.name} (Hand size: ${user.hands.size-1} -> ${user.hands.size})")
        if(drawCard is AttackCard || drawCard is DodgeCard || drawCard is PeachCard) {
            user.handSizeOffset += 1
            user.hands.remove(drawCard)
            pile.add(drawCard)
        }
    }
}

class WondrousTalentAbility : Ability() {
    override val name: String = "Wondrous Talent"
    override val description: String =
        "Ignore range when you play a tactics card. Other heroes cannot discard your armor and treasure cards in your equipment zone."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}


class BalanceOfPowerAbility : Ability() {
    override val name: String = "Balance of Power"
    override val description: String =
        "Once per main phase, you may discard any number of card in your hand and equipment zone and draw that many cards. If you discard all cards in your hand this way, draw that many cards plus 1 instead."

    override fun use(user: Character, target: Character?) {

    }
}

class RescueAbility : Ability() {
    override val name: String = "Rescue"
    override val description: String =
        "Whenever other Wu heroes play a “Peach” , if they have greater health than you, they can choose to let you gain 1 health instead, then that hero draws a card."

    override fun use(user: Character, target: Character?) {
        // This ability is triggered automatically when other Wu heroes use a "Peach" card.
        // Implementation details should be handled in the game logic rather than in this method.
    }
}

// Wu Heroes' Abilities
class SurpriseRaidAbility : Ability() {
    override val name: String = "Surprise Raid"
    override val description: String =
        "You may draw two cards. If you do, you must place two cards from your hand on top of your deck in any order."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class SelfRestraintAbility : Ability() {
    override val name: String = "Self-Restraint"
    override val description: String =
        "If you did not play or use any “Attack” during your main phase, you may skip your discard phase"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class SacrificialInjuryAbility : Ability() {
    override val name: String = "Sacrificial Injury"
    override val description: String =
        "Once per main phase, you may discard a card from hand or equipment zone and lose 1 health."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class DashingHeroAbility : Ability() {
    override val name: String = "Dashing Hero"
    override val description: String =
        "(Passive) During your drawing phase, draw an additional card. Your hand size is equal to your max health"

    override fun use(user: Character, target: Character?) {
        // This ability is passive, so no implementation needed
    }
}

class SowDiscordAbility : Ability() {
    override val name: String = "Sow Discord"
    override val description: String =
        "Once per main phase, you may reveal a card from your hand and give it to another hero. He choose one of the following:\n1. Reveal all cards in his hand and discard all cards in his hand and equipment zone with the same color.\n2. Lose 1 health."

    override fun use(user: Character, target: Character?) {
        val indexCard: Card?

        if (user.hands.isEmpty()) {
            debug("${user.name} has no hand to activate || Sow Discord ||")
            return
        }

        if (target == null) {
            return
        }

        indexCard = user.hands[0]
        target.hands.add(indexCard)
        user.hands.remove(indexCard)

        if (target.hands.size > 1 && target.hp > 1) {
            // Reveal all cards in his hand and discard all cards in his hand and equipment zone with the same color.
            println("|| Sow Discord ||: ${user.name} gives ${target.name} a \"${indexCard.name}\" with suit \"${indexCard.suit}\"")

            // Print out the remaining cards in the target's hand
            debug("\t${target.name}'s hand: ")
            for (card in target.hands) {
                debug("\t\t${card.name} (Suit: ${card.suit})")
            }

            target.hands.filter { it.suit == indexCard.suit }
            val handIterator = target.hands.iterator()
            while (handIterator.hasNext()) {
                val card = handIterator.next()
                if (card.suit == indexCard.suit) {
                    handIterator.remove()
                    println("\t${target.name} discards \"${card.name}\" (Suit: ${card.suit}) from his hand.")
                }
            }
            val equipmentIterator = target.equipmentList.iterator()
            while (equipmentIterator.hasNext() == true) {
                val card = equipmentIterator.next()
                if (card?.suit == indexCard.suit) {
                    val tmpCard = card as EquipmentCard
                    tmpCard.removeEquipment(target)
                    println("\t${target.name} discards \"${card.name}\" (Suit: ${card.suit}) from his equipment zone.)")
                }
            }

            // Print out the remaining cards in the target's hand
            debug("\t${target.name}'s hand: ")
            for (card in target.hands) {
                debug("\t\t${card.name} (Suit: ${card.suit})")
            }
        } else {
            // Lose 1 health
            target.takeDamage(null, AttackCard(), 1)
        }

    }
}

class NationalBeautyAbility : Ability() {
    override val name: String = "National Beauty"
    override val description: String = "Once per main phase, you may choose one of the following:\n" +
            "1. Treat and play a ♦ card in your hand or equipment zone as “Acedia”\n" +
            "2. Discard a ♦ card in your hand or equipment zone and then discard an “Acedia” in play\n" +
            "After the choice has been made, you draw a card."

    override fun use(user: Character, target: Character?) {
        val hand = user.hands

        if (hand.isEmpty()) {
            debug("There is no hand to activate || National Beauty ||")
            return
        }

        if (hand.any { it.suit == "Diamonds" }) {
            // option 1: treat and play a ♦ card in your hand or equipment zone as "Acedia"
            val diamondCard = hand.find { it.suit == "Diamonds" } ?: return // if no ♦ card in hand, return early

            val acedia = createCard("Acedia") as Acedia
            acedia.suit = diamondCard.suit
            acedia.number = diamondCard.number

            user.hands.remove(diamondCard)
            pile.add(diamondCard)

            target!!.dTacticsZone.add(acedia)

            println("|| National Beauty ||: ${user.name} treated and played a ${diamondCard.name} ( ♦ ) as ${acedia.name} to ${target.name}. ( Hands: ${hand.size + 1} -> ${hand.size} )")
            println("${target.name}'s Delay Tactics Zone: ${target.dTacticsZone.joinToString { "[" + it.name + "] " }}")

        } else {
            debug("There is no ♦ card in hand to activate || National Beauty ||")
            return

        }

        user.drawCard()
    }
}

class DisplaceAbility : Ability() {
    override val name: String = "Displace"
    override val description: String =
        "Whenever you became the target of an “Attack”, you may discard a card from your hand or equipment zone. If you do, choose another target hero (except the attacker) within your attack range, that hero becomes the new target of the “Attack”"

}

class HumilityAbility : Ability() {
    override val name: String = "Humility"
    override val description: String =
        "Whenever a delayed tactics card in your judgment zone is triggered or you are targeted by a non-delay tactics card played by another hero, you may remove all cards in your hand from game and put them onto your hero card. At the end of your turn, return all the cards to your hand."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class OneAfterAnotherAbility : Ability() {
    override val name: String = "One after another"
    override val description: String =
        "Whenever you lose cards in your hand, if you have no cards in your hand, you may target X heroes and each of them draw a card. (X is the number of cards you have lost.)"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class MarriageAbility : Ability() {
    override val name: String = "Marriage"
    override val description: String =
        "Once per main phase, you may choose a male hero, then discard a card from hand or move an equipment card from your equipment zone to his corresponding equipment zone (Cannot replace equipment). If you do, the hero with greater health draws a card and the one with less health gains 1 health."

    private fun afterEffect(user: Character, target: Character?) {
        if (user.hp > target!!.hp) {
            println("transferEquipment --> ${user.name} Draw Card!!!")
            user.drawCard()
            println("transferEquipment --> ${target.name} Heal!!!")
            target.heal(1)
        } else {
            println("transferEquipment --> ${target.name} Draw Card!!!")
            target.drawCard()
            println("transferEquipment --> ${user.name} Heal!!!")
            user.heal(1)
        }
    }

    private fun transferEquipment(user: Character, target: Character?) {
        println("Debug: transferEquipment")
        println("Alliance: ${target?.name}")
        when (val equipment = user.hands.first { it is EquipmentCard }) {
            is WeaponCard -> {
                target?.equipmentList?.set(0, equipment)
                user.hands.remove(equipment)
                println("${target?.name} Weapon equipment replace [${target?.equipmentList?.get(0)}] ")
                println("Updated ${target?.name} equipment list: ${target?.equipmentList?.joinToString()}")
                afterEffect(user, target)
            }
            is ArmorCard -> {
                target?.equipmentList?.set(1, equipment)
                user.hands.remove(equipment)
                println("${target?.name} Armor equipment replace [${target?.equipmentList?.get(1)}] ")
                println("Updated ${target?.name} equipment list: ${target?.equipmentList?.joinToString()}")
                afterEffect(user, target)
            }

            is MountCard -> {
                if (equipment.name == "Red Hare" || equipment.name == "Da Yuan") {
                    target?.equipmentList?.set(2, equipment)
                    user.hands.remove(equipment)
                    println("${target?.name} Mount+1 equipment replace [${target?.equipmentList?.get(2)}] ")
                    println("Updated ${target?.name} equipment list: ${target?.equipmentList?.joinToString()}")
                    afterEffect(user, target)
                } else if (equipment.name == "Hua Liu" || equipment.name == "The Shadow") {
                    target?.equipmentList?.set(3, equipment)
                    user.hands.remove(equipment)
                    println("${target?.name} Mount-1 equipment replace [${target?.equipmentList?.get(3)}] ")
                    println("Updated ${target?.name} equipment list: ${target?.equipmentList?.joinToString()}")
                    afterEffect(user, target)
                }
            }
        }
    }

    private fun removeCard(user: Character, target: Character?) {
        if (user.hands.size > 0) {
            pile.add(user.hands.removeFirst())
            println("Sun Shang Xiang remove a card")
            afterEffect(user, target)
        }
    }

    override fun use(user: Character, target: Character?) {
        val action = mutableListOf(removeCard(user, target))
        if(character.filterNot { user.enemyDistance.containsKey(it) }.any { it.gender == "M" }) {
            if (user.hands.isNotEmpty() && user.hands.any { it is EquipmentCard }) {
                action.add(transferEquipment(user, target))
            }
        }
        action.shuffled().first()
    }
}

class WarriorPrincessAbility : Ability() {
    override val name: String = "Warrior Princess"
    override val description: String = "Whenever a card is removed from your equipment zone, draw 2 cards."
    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}


class VillainousHeroAbility : Ability() {
    override val name: String = "Villainous Hero"
    override val description: String = "Whenever you are dealt damage, you\n" +
            "may draw a card. Then if the damage is\n" +
            "dealt through a card’s effect, you put\n" +
            "that card into your hand"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class EscortAbility : Ability() {
    override val name: String = "Escort"
    override val description: String = "(Emperor ability) Whenever you need to\n" +
            "play or use a “Dodge”, other Wei heroes\n" +
            "can play or use a “Dodge” for you"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class RetaliationAbility : Ability() {
    override val name: String = "Retaliation"
    override val description: String = "Whenever a hero deals\n" +
            "damage to you, for each damage you\n" +
            "have taken, you may put 1 card from his\n" +
            "hand or equipment zone into your hand"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class DemonicTalentAbility : Ability() {
    override val name: String = "Demonic talent"
    override val description: String = "Whenever a judgment\n" +
            "card is revealed, you may replace that\n" +
            "card with a card from your hand or\n" +
            "equipment zone"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class StaunchAbility : Ability() {
    override val name: String = "Staunch"
    override val description: String = "Whenever you are dealt damage, for\n" +
            "each damage you have taken, you may\n" +
            "perform a judgment.\n" +
            "If the result is red, you deal 1 damage to\n" +
            "the source of damage; if the result is\n" +
            "black, you discard a card from his hand\n" +
            "or equipment zone"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class AmbushAbility : Ability() {
    override val name: String = "Ambush"
    override val description: String = "During your draw phase, you may draw\n" +
            "X cards less, then target X heroes\n" +
            "you take a card from each target hero’s\n" +
            "hand and put them into your hand"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class BareChestedAbility : Ability() {
    override val name: String = "Bare-chested"
    override val description: String = "At the start of your draw phase, reveal\n" +
            "the top 3 cards of the library. You may\n" +
            "put all basic cards, “Duel” and weapon\n" +
            "cards into your hand and discard the\n" +
            "rest. If you do, skip your draw and until\n" +
            "the start of your next turn, Damage of\n" +
            "your “Attack” and “Duel” is increased by\n" +
            "1."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class HeavenEnvyAbility : Ability() {
    override val name: String = "Heaven’s Envy"
    override val description: String = "Whenever your judgment is resolved,\n" +
            "you may put that judgment card into\n" +
            "your hand"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class BestowedRouseAbility : Ability() {
    override val name: String = "Bestowed Rouse"
    override val description: String = "Whenever you are dealt damage, for\n" +
            "each damage you receive, draw 2 cards.\n" +
            "Then you may give a maximum of 2\n" +
            "cards to a maximum of 2 heroes. They\n" +
            "put the cards into their hand."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class GorgeousBeautyAbility : Ability() {
    override val name: String = "Gorgeous Beauty"
    override val description: String = "You may play or use any black cards in\n" +
            "your hand as “Dodge”"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class GoddessOfLuoAbility : Ability() {
    override val name: String = "Goddess of Luo"
    override val description: String = "At the beginning of your upkeep, you\n" +
            "may perform a judgement, if the result\n" +
            "is black, put that card into your hand\n" +
            "and repeat this process. Cards you get\n" +
            "from “Goddess of Luo” this turn do not\n" +
            "count towards your hand limit."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class FirstAidAbility : Ability() {
    override val name: String = "First Aid:"
    override val description: String = "If it is not your turn, you may play red\n" +
            "cards in your hand and equipment zone\n" +
            "as “Peach”"

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class TheBookOfGreenVesicleAbility : Ability() {
    override val name: String = "The Book of Green Vesicle"
    override val description: String = "Once per turn, you may discard a card in\n" +
            "hand and target a hero, that hero gains\n" +
            "1 health. If the card you discard is red,\n" +
            "you may activate this ability again, but\n" +
            "cannot target the same hero."

    override fun use(user: Character, target: Character?) {
        // implementation details
    }
}

class MatchlessAbility : Ability() {
    override val name: String = "Matchless"
    override val description: String = "(Paasive) If you are the source of an\n" +
            "“Attack”, the defending player need to\n" +
            "respond with 2 “Dodge” instead of one\n" +
            "to cancel\n" +
            "If you are involved in a duel, the other\n" +
            "player needs to use 2 “Attack” in\n" +
            "respond to your “Attack”"

    override fun use(user: Character, target: Character?) {
    }
}
class WedgeDrivingAbility : Ability() {
    override val name: String = "Wedge Driving"
    override val description: String = "Once per main phase, you may choose 2\n" +
            "male heroes and discard a card from\n" +
            "hand or equipment zone. If you do,\n" +
            "treat this as one of them play a “Duel”\n" +
            "targeting the other. (You choose who is\n" +
            "the user and who is the target)"

    override fun use(user: Character, target: Character?) {
        if (target != null) {
            if (user.gender != "M" || target.gender != "M") {
                throw IllegalArgumentException("Only male heroes can use Wedge Driving ability")
            }
        }
    }
} // not completed

class EclipseAbility : Ability() {
    override val name: String = "Eclipse"
    override val description: String = "At the end of your turn, you may draw a\n" +
            "card. If you have no cards in your hand,\n" +
            "draw 2 cards instead."

    override fun use(user: Character, target: Character?) {
        if (user.hands.isEmpty()) {
            repeat(2) {
                user.drawCard()
            }
        } else {
            user.drawCard()
        }
    }
}






