import kotlin.random.Random

class LiuBei(r: Role) : MonarchHero(Monarch()) {
    override var name = "Liu Bei"
    override var gender = "M"
    override var faction = "Shu"
    override var title = "Hero Through Troubled Times"
    var nextHandler: LiuBeiHandler? = null
    var wantAttack = false
    var isDuel = false

    //val attackTarget = this.getClosestEnemy()
    override var abilityList = listOf(KindnessAbility(), RouseAbility())


    override fun attack(target: Character): Boolean {
        if (useAttackCard() && nextHandler != null) {
            return nextHandler!!.LiuBeiHandle(target)
        } else {
            // Kindness Ability
            var handler = nextHandler
            while (handler != null && hp < maxHP) {
                if (handler is ShuHero) {
                    if (hands.size >= 2) {
                        val cards = hands.take(2).toList()
                        hands.removeAll(cards)
                        handler.hands.addAll(cards)
                        println("Kindness Ability triggered")
                        for (i in cards) {
                            println("Liu Bei gave ${i.name} to ${handler.name}")
                        }
                        println("Liu Bei heal himself with Kindness Ability")
                        hp++
                    }
                    break
                }
                handler = handler.getNext()
            }
            return super.attack(target)
        }
    }


}

class GuanYu(r: Role) : ShuHero(r) {
    override var name = "Guan Yu"
    override var gender = "M"
    override var hp = 4
    override var faction = "Shu"
    override var title = "Bearded Gentleman"
    var wantAttack = false
    override var abilityList: List<Ability> = listOf(SaintWarriorAbility())

    override fun useAttackCard(): Boolean {
        if (hands.filterIsInstance<AttackCard>().isNotEmpty()) {
            return true
        } else if (hands.filterIsInstance<AttackCard>().isEmpty() && hands.size != 0) {
            println(hands[0])
            val suit = hands[0].suit
            val num = hands[0].number
            println("Guan Yu \uD83D\uDC7A wants to change ${hands[0].name} be attack card : ${hands[0].name} (${hands[0].suit}) ")
            return if (hands[0].suit == "Hearts" || hands[0].suit == "Diamonds") {
                hands.set(0, AttackCard("Attack"))
                hands[0].suit = suit
                hands[0].number = num
                println(hands[0])
                println(hands)
                println("Changed after : ${hands[0].name} (${hands[0].suit}) *************")
                true
            } else {
                false
            }
        }
        return false
    }

    override fun chooseCard(hero: Character, cards: List<Card>): Card {
        println(hero.hands)
        val redCard = cards.random()
        if (redCard.suit == "Diamonds") {
            range = 999
            println("My Attack Range is 999!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        }
        return cards.random()
    }

    override fun drawPhase() {
        super.drawPhase()
        range = 1
    }
}

class ZhangFei(r: Role) : ShuHero(r) {
    override var name = "Zhang Fei"
    override var gender = "M"
    override var hp = 4
    override var faction = "Shu"
    override var title = "A match for ten thousand warriors"
    override var abilityList: List<Ability> = listOf(RoarAbility())
}

class ZhugeLiang(r: Role) : ShuHero(r) {
    override var name = "Zhuge Liang"
    override var gender = "M"
    override var hp = 3
    override var faction = "Shu"
    override var title = "Prime minister who has passed his prime"
    override var abilityList: List<Ability> = listOf(StargazingAbility(), EmptyFortAbility())

    override fun judgmentPhase() {
        if (isDead || !checkWin())
            return

        // Create a variable to store the alive
        val numAlive = character.count { !it.isDead }
        val starCnt= if (numAlive > 3) numAlive else 3
        val revealCards : List<Card> = library.revealCards(starCnt)

        println("Zhuge Liang's || Stargazing \uD83C\uDF1F ||: Reveal $starCnt cards from the top of the deck.")
        // For loop to print the revealed cards
        for (i in revealCards)
            println("\tRevealed card: ${i.name} (${i.suit}, ${i.number})")

        // Sort the revealCards in the order of suit: 'Hearts', 'Diamonds', 'Clubs', 'Spades'
        val sortedCards = revealCards.sortedBy {
            when (it.suit) {
                "Hearts" -> 1
                "Diamonds" -> 2
                "Clubs" -> 3
                else -> {4}
            }
        }

        println("$name put the revealed cards back to the top of the deck as following orders:")
        // For loop to print the sorted cards
        for (i in sortedCards) {
            println("\tSorted card: ${i.name} (${i.suit}, ${i.number}) ")
        }

        // Add back the sortedCards to the deck's top
        deck.addAll(0, sortedCards)

        super.judgmentPhase()
    }
    override fun endPhase() {
        super.endPhase()
        distanceOffset = if(hands.isEmpty()) 0 else 999
    }
}

class ZhangYun(r: Role) : ShuHero(r) {
    override var name = "Zhang Yun"
    override var gender = "M"
    override var hp = 4
    override var faction = "Shu"
    override var title = "The Young General"
    override var abilityList: List<Ability> = listOf(DragonCourageAbility())

    override fun useDodgeCard(target: Character): Boolean {
        if (target.equipmentList[1] is EightTrigrams) {
            if (blueSteelSword_effect) {
                println("${target.name} cannot use <Eight Trigrams \uD83D\uDEE1️️> since <Blue Steel Sword> is activated:")
                println("<Blue Steel Sword> Ignore the effect of armor equipped on that hero")
            } else {
                val card = deck.removeFirst()
                println("${target.name}'s <Eight Trigrams \uD83D\uDEE1️>: Effect Activated!")
                println(
                    "\"If the result is RED, it is considered to " +
                            "have played or discarded a 'Dodge'\""
                )
                println("${target.name} revealed card with '${card.suit}' suit")
                pile.add(card)
                if (card.suit == "Hearts" || card.suit == "Diamonds") {
                    println("${target.name} successfully dodged the attack by <Eight Trigrams \uD83D\uDEE1>️")
                    return true
                }
            }
        }
        // Debug: Print every card's name in hands
        debug("Hands: ${hands.joinToString(", ")}")

        if (hands.filterIsInstance<DodgeCard>().isNotEmpty()) {
            val dodgeCard = hands.filterIsInstance<DodgeCard>().first()
            playCard(dodgeCard, target)
            return true
        } else if (hands.filterIsInstance<DodgeCard>().isEmpty() && hands.filterIsInstance<AttackCard>().isNotEmpty()) {
            val attackCardIndex = hands.indexOf(hands.filterIsInstance<AttackCard>().first())
            val attackCard = hands.filterIsInstance<AttackCard>().first()
            println("Zhang Yun want to use ${attackCard.name} be dodge card : ${hands[attackCardIndex]} ${attackCard.name} (${attackCard.suit} : ${attackCard.number})")
            hands[attackCardIndex] = DodgeCard("Dodge")
            attackCard.name = hands[attackCardIndex].name
            println("Zhang Yun want to use dodge card : ${hands[attackCardIndex]} ${attackCard.name} (${attackCard.suit} : ${attackCard.number})")
            playCard(hands[attackCardIndex], target)
            return true
        }
        return false
    }

    override fun useAttackCard(): Boolean {
        if (hands.filterIsInstance<AttackCard>().isNotEmpty()) {
            return true
        } else if (hands.filterIsInstance<AttackCard>().isEmpty() && hands.filterIsInstance<DodgeCard>().isNotEmpty()) {
            val DodgeCardIndex = hands.indexOf(hands.filterIsInstance<DodgeCard>().first())
            val DodgeCard = hands.filterIsInstance<DodgeCard>().first()
            println("Zhang Yun want to use ${DodgeCard.name} be Attack card : ${hands[DodgeCardIndex]} ${DodgeCard.name} (${DodgeCard.suit} : ${DodgeCard.number})")
            hands[DodgeCardIndex] = AttackCard("Attack")
            DodgeCard.name = hands[DodgeCardIndex].name
            println("Zhang Yun want to use Attack card : ${hands[DodgeCardIndex]} ${DodgeCard.name} (${DodgeCard.suit} : ${DodgeCard.number})")
            return true
        } else {
            return false
        }
    }
}

class MaChao(r: Role) : ShuHero(r) {
    override var name = "Ma Chao"
    override var gender = "M"
    override var hp = 4
    override var faction = "Shu"
    override var title = "Lone horseman that can outmatch a thousand troops"
    override var abilityList: List<Ability> = listOf(HorsemanshipAbility(), IronCalvaryAbility())
}

class HuangYueYing(r: Role) : ShuHero(r) {
    override var name = "Huang Yue Ying"
    override var gender = "F"
    override var hp = 3
    override var faction = "Shu"
    override var title = "Veiled Heroine"
    var lockEquipment = false
    override var abilityList: List<Ability> = listOf(GatheringWisdomAbility(), WondrousTalentAbility())

}

class SunQuan(r: Role) : MonarchHero(Monarch()) {
    override var name = "Sun Quan"
    override var gender = "M"
    override var faction = "Wu"
    override var title = "Young and Worthy Lord"
    var nextHandler: SunQuanHandler? = null
    override var abilityList = listOf(BalanceOfPowerAbility(), RescueAbility())

    override fun healHero(): Boolean {
        if (nextHandler != null) {
            return nextHandler!!.SunQuanHandle(this)
        }
        return false
    }

    override fun discardPhase() {
        val addCardList = mutableListOf<Card>().toMutableList()
        if (hands.size > 0) {
            println("|| Balance of Power \uD83D\uDCAA ||: Effect Activated!")
            println(
                "Once per main phase, you may discard any number of card in your hand and equipment " +
                        "zone and draw that many cards. If you discard all cards in your hand this way, draw " +
                        "that many cards plus 1 instead."
            )

            for (i in 0 until Random.nextInt(1, hands.size + 1)) {
                val tmp = hands.removeFirst()
                pile.add(tmp)
                addCardList.add(tmp)
                println("$name discards ${tmp.name} ( Hand: ${hands.size + 1} -> ${hands.size} )")
            }
            if (hands.size <= 0) {
                println("Sun Quan's hand size: ${hands.size}")
                hands.addAll(addCardList)
                println("Sun Quan draws 1 card ( Hands size: ${hands.size - 1} -> ${hands.size} )")
                hands.add(deck.removeFirst())
                println(addCardList)
                println("Sun Quan get back 1 more card ( Hands size: ${hands.size - 1} -> ${hands.size})")
                println("Sun Quan ${hands.size}")
            } else {
                println("Sun Quan left card: ${hands.size}")
                hands.addAll(addCardList)
//                println(addCardList)
//                println("Sun Quan get back")
//                println("Sun Quan ${hands.size}")
            }
        }
        super.discardPhase()
    }
}

class GanNing(r: Role) : WuHero(r) {
    override var name = "Gan Ning"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wu"
    override var title = "Pirate with Silk Sails"
    override var abilityList: List<Ability> = listOf(SurpriseRaidAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    // During your main phase, you may play any black cards in your hand and equipment
    // zone as “Burn Bridges”
    override fun useTacticCard() {
        super.useTacticCard()

        val blackSuits = setOf("Spades", "Clubs")
        val blackCards = hands.filterIsInstance<Card>().filter { it.suit in blackSuits }

        if (blackCards.isNotEmpty()) {
            for (blackCard in blackCards) {
                val tempBurnBridges = BurnBridges()
                println("|| Surprise Raid \uD83D\uDCAA ||: $name use ${blackCard.name} (${blackCard.suit}) as <Burn Bridges>\t(Hands: ${hands.size + 1} -> ${hands.size})")
                tempBurnBridges.effect(this, getClosestEnemy())
                hands.remove(blackCard)
                pile.add(blackCard)
            }
        }
    }
}

class LuMeng(r: Role) : WuHero(r) {
    override var name = "Lu Meng"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wu"
    override var title = "The Rising Underdog"
    override var abilityList: List<Ability> = listOf(SelfRestraintAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    override fun discardPhase() {
        if (!attacked) {
            println("Lu Meng cannot discard phase since he has not attacked")
            return
        }
        super.discardPhase()
        attacked = false
    }
}

class HuangGai(r: Role) : WuHero(r) {
    override var name = "Huang Gai"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wu"
    override var title = "Sacrificing for the Country"
    override var abilityList: List<Ability> = listOf(SacrificialInjuryAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    override fun discardPhase() {
        if (hands.isNotEmpty()) {
            hands.removeFirst()
            println("Sacrificial Injury: Discard a card!!!!!!!!!!!!!!!!!\n( Hands: ${hands.size + 1} -> ${hands.size} )")
            if (hp <= 1) {
                takeDamage(this, null, 1)
            } else {
                takeDamage(this, null, 1)
                super.discardPhase()
            }
        }
    }
}

class ZhouYu(r: Role) : WuHero(r) {
    override var name = "Zhou Yu"
    override var gender = "M"
    override var hp = 3
    override var faction = "Wu"
    override var title = "Wu Viceroy"
    override var abilityList: List<Ability> = listOf(DashingHeroAbility(), SowDiscordAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    // Dashing Hero
    override fun drawPhase() {
        super.drawPhase()
        println("|| Dashing Hero \uD83D\uDCAA ||: Draw an additional card.")
        drawCard()
    }

    override fun discardPhase() {
        if (isDead || !checkWin())
            return

        println("* Discard Phrase")
        println(
            "|| Dashing Hero \uD83D\uDCAA ||: Your hand size is equal\n" +
                    "to your max health."
        )
        if (hands.size <= maxHP) {
            println("\t${name}'s maxHP is ${hp}, number of cards is ${hands.size}. No need to discard cards.")
            return
        }

        //!!!: The method below SIMPLY RANDOMLY discard hands <- Selfish Algo.
        while (hands.size > hp && hp > 0) {
            println("\t${name}'s maxHP is ${hp}, now have ${hands.size} cards.")
            discard()
        }

        println("\t${name}'s maxHP is ${hp}, now have ${hands.size} cards.")
    }

    // Sow Discord
    override fun useAbility() {
        super.useAbility()
        SowDiscordAbility().use(this, getClosestEnemy())
    }
}

class DaQiao(r: Role) : WuHero(r) {
    override var name = "Da Qiao"
    override var gender = "F"
    override var hp = 3
    override var faction = "Wu"
    override var title = "Archery Princess"
    override var abilityList = listOf(NationalBeautyAbility(), DisplaceAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    override fun useAbility() {
        super.useAbility()
        NationalBeautyAbility().use(this, getClosestEnemy())
    }
}

class LuXun(r: Role) : WuHero(r) {
    override var name = "Lu Xun"
    override var gender = "M"
    override var hp = 3
    override var faction = "Wu"
    override var title = "The meek scholar with Valiant talents"
    override var abilityList = listOf(HumilityAbility(), OneAfterAnotherAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }
}

class SunShangXiang(r: Role) : WuHero(r) {
    override var name = "Sun Shang Xiang"
    override var gender = "F"
    override var hp = 3
    override var faction = "Wu"
    override var title = "Archery Princess"
    override var abilityList = listOf(MarriageAbility(), WarriorPrincessAbility())

    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    override fun useAbility() {
        super.useAbility()
        if(character.filterNot { this.enemyDistance.containsKey(it) }.any { it.gender == "M" }){
            val alliance = character.filterNot { this.enemyDistance.containsKey(it) }.filter { it.gender == "M" }.toMutableList().shuffled().first()
            MarriageAbility().use(this,alliance)
        }
    }
}

class CaoCao(r: Role) : MonarchHero(Monarch()) {
    override var name = "Cao Cao"
    override var gender = "M"
    override var faction = "Wei"
    override var title = "Hero Through Troubled Times"
    var nextHandler: CaoCaoHandler? = null
    override var abilityList = listOf(VillainousHeroAbility(), EscortAbility())

    override fun dodgeAttack(): Boolean {
        if (nextHandler != null) {
            return nextHandler!!.handle()
        }
        return false
    }
}

class SimaYi(r: Role) : WeiHero(r) {
    override var name = "Sima Yi"
    override var gender = "M"
    override var hp = 3
    override var faction = "Wei"
    override var title = "Cunning Strategist"
    override var abilityList = listOf(RetaliationAbility(), DemonicTalentAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    override fun takeDamage(user: Character?, card: Card?, amount: Int) {
        super.takeDamage(user, card, amount)
        if (user != null && user.hands.size > 1) {
            val userCards = user.hands + user.equipmentList
            if (userCards.isNotEmpty()) {
                val randomCard = userCards.random()
                if (randomCard is Card) {
                    println("Triggered Retaliation")
                    println("$name uses Retaliation on ${user.name} and steals ${randomCard.name}")
                    user.hands.add(randomCard)
                    if (randomCard in user.hands) {
                        user.hands.remove(randomCard)
                        println("$name discards ${randomCard.name} from ${user.name} hand due to Retaliation")
                    } else if (randomCard in user.equipmentList) {
                        val index = user.equipmentList.indexOf(randomCard)
                        user.equipmentList[index] = null
                        println("$name discards ${randomCard.name} from ${user.name} equipment list")
                    }
                }
            }
        } else {
            println("$user has no card for Si ma Yi to use Retaliation")

        }
    }
}

class XiahouDun(r: Role) : WeiHero(r) {
    override var name = "Xiahou Dun"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wei"
    override var title = "Mighty General"
    override var abilityList: List<Ability> = listOf(StaunchAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    override fun takeDamage(user: Character?, card: Card?, amount: Int) {
        super.takeDamage(user, card, amount)
        println("* Judgment Phrase from Staunch Ability")
        val firstCard = deck.removeFirst()
        println("The card picked from deck with suit [${firstCard.suit}]")
        if (firstCard.suit == "Hearts" || firstCard.suit == "Diamonds") {
            user?.takeDamage(this, null, 1)
            println("${user?.name} reduce 1 hp due to Staunch Ability")
        } else {
            if (user != null) {
                val userStateList = user.stateList + user.equipmentList.filterNotNull()
                val userAllCards = userStateList + user.hands
                if (userAllCards.isNotEmpty()) {
                    // Discard user equipment
                    val equipmentCards = userAllCards.filterIsInstance<EquipmentCard>()
                    if (equipmentCards.isNotEmpty() && user.equipmentList.indexOf(equipmentCards.last()) > -1) {
                        user.equipmentList[user.equipmentList.indexOf(equipmentCards.last())] = null
                        println("$name used Staunch Ability to discard ${equipmentCards.last().name} from ${user.name}'s equipment list")
                        return
                    }
                    // Discard user delay tactics card
                    val delayTacticsCards = userAllCards.filterIsInstance<DelayTacticsCard>()
                    if (delayTacticsCards.isNotEmpty()) {
                        user.stateList.remove(delayTacticsCards.last())
                        println("$name used Staunch Ability to discard ${delayTacticsCards.last().name} from ${user.name}'s state list")
                        return
                    }
                    // Discard a card from user hero's hand
                    val userHandCards = user.hands.toMutableList()
                    if (userHandCards.isNotEmpty()) {
                        val discardedCard = userHandCards.removeLast()
                        user.hands = userHandCards
                        println("$name used Staunch Ability to discard ${discardedCard.name} from ${user.name}'s hand")
                        return
                    }
                }
            }
        }

    }
}

class ZhangLiao(r: Role) : WeiHero(r) {
    override var name = "Zhang Liao"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wei"
    override var title = "Fierce General"
    override var abilityList: List<Ability> = listOf(AmbushAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    // Ability: Ambush
    override fun drawPhase() {
        if (isDead || !checkWin())
            return

        println("* Draw Phrase")
        println(
            "\t|| Ambush \uD83D\uDCAA ||: During your draw phase, you may draw X cards less," +
                    "\n\tthen target X heroes. You take a card from each target hero’s hand and put them into your hand."
        )

        getEnemyDistance()
        val enemyList = enemyDistance.keys.toList()
        println("enemyList: ${enemyList.size}")
        var cardCnt = 2

        for (i in enemyList) {
            // Debug to print every card in enemy's hand
            debug("${i.name}'s hand: ${i.hands.size}")
            if (i.hands.size > 0) {
                hands.add(i.hands.removeAt(0))
                cardCnt--
                println("$name takes a card from ${i.name}'s hand.")
            }
        }

        for (i in 0 until cardCnt)
            if (deck.size > 0)
                drawCard()

        // Change haveAttacked's value to false
        haveAttacked = false
        isAbandon = false
        blueSteelSword_effect = false
    }
}

class XuChu(r: Role) : WeiHero(r) {
    override var name = "Xu Chu"
    override var gender = "M"
    override var hp = 4
    override var faction = "Wei"
    override var title = "Mighty General"
    override var abilityList: List<Ability> = listOf(BareChestedAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    override fun drawPhase() {
        if (isDead || !checkWin())
            return

        // Get 3 cards from the top of the deck
        val topCards = library.revealCards(3)

        println("Xu Chu activates || Bare Chested \uD83D\uDCAA ||!")
        println("\tReveal 3 cards from the deck.")

        // Print the cards
        topCards.forEachIndexed { index, card ->
            println("\t${index + 1}. ${card.name}")
        }

        // Separate the cards into useful and useless cards
        val (usefulCards, uselessCards) = topCards.partition { card ->
            card is AttackCard || card is DodgeCard || card is PeachCard || card is Duel || card is WeaponCard
        }

        // Print the useful cards
        print("$name adds cards: ")
        if (usefulCards.isEmpty())
            print("None ")
        usefulCards.forEach { card ->
            print("[${card.name}] ")
            hands.add(card)
        }
        println("into hands. Rest of other cards are discarded.")
        pile.addAll(uselessCards)

        if (usefulCards.isNotEmpty()) {

            println("$name's Damage of Attack and Duel is increased by 1 until the start of their next turn!")
            dealDamageOffset += 1
        } else {
            println("That's a bad draw for Xu Chu!")
        }
    }
}

class GuoJia(r: Role) : WeiHero(r) {
    override var name = "Guo Jia"
    override var gender = "M"
    override var hp = 3
    override var faction = "Wei"
    override var title = "Mighty General"
    override var abilityList: List<Ability> = listOf(HeavenEnvyAbility(), BestowedRouseAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    // Heaven Envy is done in check() of DelayTactics and EightDiagram
    // Bestowed Rouse is done in the takeDamage() of Character
}

class ZhenJi(r: Role) : WeiHero(r) {
    override var name = "Zhen Ji"
    override var gender = "F"
    override var hp = 3
    override var faction = "Wei"
    override var title = "Mighty General"
    override var abilityList: List<Ability> = listOf(GorgeousBeautyAbility(), GoddessOfLuoAbility())
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    override fun judgmentPhase() {
        println("$name activates || Goddess of Luo \uD83D\uDC83 ||")

        while(true){
            val checkCard = deck.removeFirst()
            println("Reveal a card from deck: ${checkCard.name} (${checkCard.suit})")
            if (checkCard.suit == "Spades" || checkCard.suit == "Clubs"){
                println("\t||　Goddess of Luo \uD83D\uDC83 ||: ${checkCard.name} is BLACK, put that card into $name's hand.")
                hands.add(checkCard)
            } else {
                println("\t|| Goddess of Luo \uD83D\uDC83 ||: ${checkCard.name} is RED, put that card into pile.")
                pile.add(checkCard)
                break
            }
        }
        super.judgmentPhase()
    }
}

class HuaTuo(r: Role) : AdvisorHero(r) {
    override var name = "Hua Tuo"
    override var gender = "M"
    override var faction = "Kingdomless"
    override var title = "Sage of Medicine"
    override var abilityList: List<Ability> = listOf(FirstAidAbility(), TheBookOfGreenVesicleAbility())

    override fun discardPhase() {
        val healHero = character.filter { it.hp < it.maxHP && it.name != this.name }.toMutableList()
        println(healHero)
        while (hands.size > 0 && healHero.size != 0) {
            if (hands.any { it.suit == "Hearts" || it.suit == "Diamonds" }) {
                pile.add(hands.removeFirst())
                healHero.shuffled().first().heal(1)
                println("Hua Tuo discard a red card \uD83D\uDD34 to heal ${healHero.first().name}")
                healHero.removeFirst()
            } else {
                pile.add(hands.removeFirst())
                healHero.shuffled().first().heal(1)
                println("Hua Tuo discard a normal card \uD83C\uDCCF to heal ${healHero.first().name}")
                break
            }
        }
        super.discardPhase()
    }
}

class LuBu(r: Role) : Character(r) {
    override var name = "Lu Bu"
    override var gender = "M"
    override var hp = 5;
    override var maxHP = 5;
    override var faction = "Kingdomless"
    override var title = "Embodiment of Force"
    override var abilityList: List<Ability> = listOf(MatchlessAbility())
}

class DiaoChan(r: Role) : AdvisorHero(r) {
    override var name = "Diao Chan"
    override var gender = "F"
    override var faction = "Kingdomless"
    override var title = "Siren of the Court"
    override var abilityList: List<Ability> = listOf(WedgeDrivingAbility(), EclipseAbility())

    // Ability: Wedge Driving
    override fun useAbility() {
        if (hands.isEmpty()) {
            debug("$name has no cards in hand to use || Wedge Driving ||.")
            return
        }

        // Get two male heroes from the game
        val maleHeroes = character.filter { it.gender == "M" }
        if (maleHeroes.size < 2) {
            debug("$name is unable to use || Wedge Driving || because there are not enough male heroes in the game.")
            return
        }

        val target1 = maleHeroes.random()
        val target2 = maleHeroes.filter { it != target1 }.random()
        debug("user: $target1, target: $target2")

        println("$name activates || Wedge Driving ||.")


        // Discard a card from hand or equipment zone
        val discardCard = hands.random()
        hands.remove(discardCard)
        pile.add(discardCard)
        println("$name discard \"${discardCard.name}\" from hand.")

        val tempDuelCard = Duel()
        // target1 duel target2
        tempDuelCard.effect(target1, target2)

    }

    override fun endPhase() {
        super.endPhase()
        if (isDead || !checkWin())
            return

        println("|| Eclipse ||: At the end of your turn, you may draw a card. If you have no cards in your hand, draw 2 cards instead.")
        if (hands.size == 0) {
            println("Since $name have no cards in her hand, draw 2 cards instead.")
            drawTwoCards()
        } else
            drawCard()
    }

}






