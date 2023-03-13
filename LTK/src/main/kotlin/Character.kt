import kotlin.reflect.KClass

abstract class Character(r: Role) : Role by r {
    open var name: String = ""
    open var gender: String = ""
    open var role: Role = r
    open var maxHP: Int = 4
    open var hp: Int = maxHP
    open var isDead: Boolean = false
    open var abilityList: List<Ability> = listOf()
    open var faction: String = ""
    open var attackRange: Int = 1
    open var range: Int = 1
    open var isBarbariansAssault = false
    open var attacked = false
    open var turnEnd = false

    // This variable is used to develop some effects of cards or abilities
    open var attackRangeOffset = 0
    open var dealDamageOffset = 0
    open var distanceOffset = 0
    open var handSizeOffset = 0

    // This stateList may not be useful anymore since IT CAN BE REPLACED BY dTacticsZone and Cards' effects
    open var stateList: MutableList<Card> = mutableListOf()

    open var title: String = ""
    open var hands: MutableList<Card> = mutableListOf()
    var equipmentList = Array<Card?>(4) { null }


    // Abandon card
    open var isAbandon: Boolean = false
    open var dTacticsZone: MutableList<DelayTacticsCard> = mutableListOf()

    // open var alive: Boolean = true
    // open lateinit var enemyRange: HashMap<Character, Int>
    open var enemyDistance: HashMap<Character, Int> = hashMapOf()

    // use a variable to store have attacked or not
    open var haveAttacked: Boolean = false

    // use a variable to store have used ability or not
    open var haveUsedAbility: Boolean = false

    // use a variable to store the attacker
    open var attacker: Character? = null

    // Variable for Blue Steel Sword
    open var blueSteelSword_effect: Boolean = false

    val basicStrategy = BasicStrategy(this)

    init {
        basicStrategy.changeState(HealthyState.create(basicStrategy))
    }

    fun setHealthyState() {
        basicStrategy.changeState(HealthyState.create(basicStrategy))
    }

    fun setUnhealthyState() {
        basicStrategy.changeState(UnhealthyState.create(basicStrategy))
    }

    open fun alive(): Boolean {
        return (hp >= 0)
    }

    fun addInitialCards() {
        for (i in 1..4) {
            drawCard()
        }
    }

    fun drawCard() {
        if (deck.isNotEmpty()) {
            hands.add(deck.removeFirst())
            println("\t$name draws a card \uD83C\uDCCF. (Hand size: ${hands.size - 1} -> ${hands.size})")
        }
    }

    open fun playCard(card: Card, target: Character?) {
        if (!hands.contains(card)) {
            throw Exception("Not contain $card in $name's hands")
        } else {
            card.effect(this, target)
            hands.remove(card)
            pile.add(card)
        }
    }


    fun useAbility(target: Character? = null, ability: Ability) {
        ability.use(this, target)
    }

    fun searchAndReturnCard(hero: Character, cardType: KClass<out Card>): Card? {
        val card: Card?
        for (i in pile.size - 1 downTo 0) {
            if (pile[i]::class == cardType) {
                card = pile.removeAt(i)
                println("${card.name} is taken from pile due to Cao Cao's ability (Villainous Hero \uD83E\uDDB9\u200D♂️)")
                hero.hands.add(card)
                println("${card.name} added to ${hero.name} hands")
                return card
            }
        }

        return null
    }

    open fun takeDamage(user: Character?, card: Card?, amount: Int) {
        hp -= amount
        println("-->　$name lost $amount HP. (HP: ${hp + amount} -> $hp)")

        if (this is GuoJia) {
            println("|| Bestowed Rouse \uD83D\uDCAA || for each damage $name receive, draw 2 cards.")
            for (i in 0 until amount) {
                drawTwoCards()

                getEnemyDistance()
                // Find Allies that characters in not in EnemyRange
                val allies = character.filterNot { enemyDistance.containsKey(it) }.toMutableList()
                // use debug() to print enemies' name
                debug("$name's enemies: ${enemyDistance.keys.joinToString { it.name }}")
                // Use joinToString to print i's name
                debug("$name's allies: ${allies.joinToString { it.name }}")

                for (ally in allies) {
                    if (ally is GuoJia || ally.hp <= 0)
                        continue

                    if (hands.isNotEmpty()) {
                        val card = hands.random()
                        ally.hands.add(card)
                        hands.remove(card)
                        println("\t$name gives ${card.name} to ${ally.name} ( Hands: ${hands.size + 1} -> ${hands.size})")
                    }
                }
            }
        }

        if (hp > 0)
            return

        // get Hua Tuo in character list
        if (character.any { it is HuaTuo }) {
            val huaTuo = character.first { it is HuaTuo }
            if (huaTuo.turnEnd && huaTuo.hands.any { it.suit == "Hearts" || it.suit == "Diamonds" }) {
                val peachCardIndex =
                    huaTuo.hands.indexOf(huaTuo.hands.first { it.suit == "Hearts" || it.suit == "Diamonds" })
                val peachCard = PeachCard("Peach")
                huaTuo.hands[peachCardIndex] = peachCard
                println("Hua Tuo used a Peach Card to heal \uD83D\uDC8A\uD83D\uDC8A!!!!!!!!!!!")
                peachCard.effect(huaTuo, huaTuo)
                huaTuo.hands.remove(peachCard)
                pile.add(peachCard)
            }
        }

        if (this is CaoCao) searchAndReturnCard(this, card!!::class)

        // Print some text effects and formatting to show the character is almost DEAD
        println("\n//////////////////////////////////////// DYING ////////////////////////////////////////")
        println("$name is almost dead ☠️. HP: $hp")

        val allies = character.filterNot { enemyDistance.containsKey(it) }.toMutableList()
        // Use joinToString to print i's name
        debug("$name's allies: ${allies.joinToString { it.name }}")
        for (i in allies) {
            debug("${i.name}'s hands: ")
            debug(i.hands.joinToString { it.name })

            if (hp <= 0) {
                while (i.hands.any { it is PeachCard }) {
                    // Get the first PeachCard in my's hands
                    val peachCard = i.hands.first { it is PeachCard }
                    println("$name is healed by ${i.name}'s Peach Card .")
                    heal(1)
                    i.hands.remove(peachCard)
                    // Print the every card of i's hands
                }
            }
        }

        if (this is SunQuan) {
            healHero()
        }

        if (hp <= 0) {
            println("Nobody can save $name... \uD83D\uDE22")
        }
        println("////////////////////////////////////////////////////////////////////////////////////////\n")

        if (hp <= 0) {
            isDead = true
            println("----------------------------------------------\n$name is dead ☠️☠️☠️. He/She is a $roleTitle\n----------------------------------------------")
        } else
            isDead = false
    }

    fun heal(amount: Int) {
        hp += amount
        if (hp > maxHP) {
            hp = maxHP
            println("$name is healed \uD83E\uDE78. (HP: $hp -> $hp)")
        } else {
            println("$name is healed \uD83E\uDE78. (HP: ${hp - 1} -> $hp)")
        }
    }

    fun drawTwoCards() {
        for (i in 1..2)
            if (deck.isNotEmpty()) {
                hands.add(deck.removeFirst())
            }
        println("\t$name draws two cards \uD83C\uDCCF\uD83C\uDCCF. (Hand size: ${hands.size - 2} -> ${hands.size})")
    }

    open fun dodgeAttack(): Boolean {
        return false
    }

    open fun attackHero(): Boolean { // Liu Bei using
        return false
    }

    open fun healHero(): Boolean { // Sun Quan using
        return false
    }

    open fun beingAttacked() {
        println("\n$name got attacked")
        if (dodgeAttack()) {
            println("$name dodged attack, current hp is $hp")
        } else {
            println("$name is unable to dodge attack, current hp is ${--hp}.")
        }
    }

    open fun chooseCard(hero: Character, cards: List<Card>): Card {
        return cards.random()
    }

    open fun printStatus() {
        println("$name's cards:")
        println("Hands: ${hands.joinToString(", ")}")
        println("Equipment: ${equipmentList.joinToString(", ")}")
//        println("Judgement: ${stateList.filterIsInstance<JudgementCard>().joinToString(", ")}")
    }

    open fun useAttackCard(): Boolean {
        // Check if there is any Attack card in hand
        val attackCards = hands.filterIsInstance<AttackCard>()
        if (attackCards.isNotEmpty()) {
            return true
        }
        return false
    }

    open fun useDodgeCard(target: Character): Boolean {
        if (target.equipmentList[1] is EightTrigrams) {
            if (blueSteelSword_effect) {
                println("${target.name} cannot use <Eight Trigrams \uD83D\uDEE1️> since <Blue Steel Sword \uD83D\uDDE1️> is activated:")
                println("<Blue Steel Sword \uD83D\uDDE1️> Ignore the effect of armor equipped on that hero")
            } else {
                val card = deck.removeFirst()
                println("${target.name}'s <EightTrigrams \uD83D\uDEE1>: Effect Activated!")
                println(
                    "\"If the result is RED, it is considered to " +
                            "have played or discarded a 'Dodge'\""
                )
                println("${target.name} revealed card with '${card.suit}' suit")
                (if (target is GuoJia) target.hands else pile).add(card)

                if (card.suit == "Hearts" || card.suit == "Diamonds") {
                    println("\t${target.name} successfully dodged the attack by Eight Trigrams \uD83D\uDEE1️")
                    if (target is GuoJia) {
                        println("|| Heaven’s Envy ||: Whenever your judgment is resolved, you may put that judgment card into your hand.")
                        println("\t${target.name} put the ${card.name} into his hand.")
                    }
                    return true
                }
            }
        }
        debug("Hands: ${hands.joinToString(", ")}")
        // Check if there is any Dodge card in hand
        val dodgeCards = hands.filterIsInstance<DodgeCard>()
        // Debug: Print every card's name in hands

        if (dodgeCards.isNotEmpty()) {
            val dodgeCard = dodgeCards.first()
            playCard(dodgeCard, target)
            println("\t${target.name} dodged attack with dodge card \uD83C\uDCCF")
            return true
        }

        val blackCards = hands.filter { it.suit == "Spades" || it.suit == "Clubs" }
        if (blackCards.isNotEmpty() && target is ZhenJi) {
            val blackCard = blackCards.first()
            playCard(blackCard, target)
            println("${target.name} activates “ Gorgeous Beauty \uD83D\uDC83”")
            println("---> ${target.name} dodged attack with black card - ${blackCard.name} (${blackCard.suit})")
            return true
        }

        return false
    }

    open fun attack(target: Character): Boolean {
        // Check if who is attacking (Mainly for Guan Yu)
        target.attacker = this
        debug("target.attacker: ${target.attacker?.name}")

        if (haveAttacked) {
            println("$name has already attacked.")
            return false
        }

        if (!canAttackEnemy(target)) {
            println("$name can't attack ${target.name} since they are too far away.")
            return false
        }

        if (useAttackCard()) {
            println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTACK !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            val attackCards = hands.filterIsInstance<AttackCard>()
            val attackCard = chooseCard(this, attackCards)
            playCard(attackCard, target)
            println("$name ( HP: $hp ) attacked \uD83D\uDDE1 ${target.name} ( HP: ${target.hp} )")

            if (equipmentList[0] is TwinSwords && target.gender != this.gender) {
                println("<Twin Swords>: Effect Activated!")
                println(
                    "<Twin Swords>: Whenever you target a hero of the opposite gender with an “Attack”, that" +
                            "hero choose one of the following:\n" +
                            "\t1. Discard a card from his hand\n" +
                            "\t2. You draw a card"
                )
//                if (target.gender != this.gender) {
                if (target.hands.isNotEmpty()) {
                    println("Option 1. ${target.name} discards a card from his hand.")
                    // Discard 1 hand from target
                    target.discard()
                } else {
                    println("Option 2. ${target.name} let $name draw a card.")
                    drawCard()
                }
//                }
            }

            if (equipmentList[0] is SwordofBlueSteel) {
                println("<Blue Steel Sword \uD83D\uDDE1️ >: Passive Effect Activated!")
                println("<Blue Steel Sword \uD83D\uDDE1️ >: Ignore the effect of armor equipped on that hero.")
                target.blueSteelSword_effect = true
            }

            // (Heaven Halberd: Whenever you play an “Attack” targeting a hero, if you have no cards in your hand, you may target up to 2 additional heroes.)
            if (equipmentList[0] is HeavenHalberd && hands.isEmpty()) {
                println("< Heaven Halberd \uD83D\uDDE1️ > : Effect Activated!")
                println("< Heaven Halberd \uD83D\uDDE1️ > : Whenever you play an “Attack” targeting a hero, if you have no cards in your hand, you may target up to 2 additional heroes.")
                // Get targets from enemyDistance
                val targets = enemyDistance.filter { it.value == 1 }.keys
                for (i in targets) {
                    if (i != target) {
                        println("${i.name} is now also targeted by ${name}.")
                        dodge(i)
                    }
                }
            }

            // DaQiao's Displace:
            if (target is DaQiao && target.hands.size != 0) {
                val newTarget = target.getClosestEnemy()

                if (newTarget != null && newTarget != this) {
                    println("${target.name} activated || Displace ||:")

                    val card = (target.hands.first())
                    target.hands.remove(card)
                    println("\tWhenever you play an “Attack” targeting a hero, ${target.name} may discard a card from her hand or equipment zone.")

                    println("\t(${target.name} discarded ${card.name} from her hand. ( Hands: ${target.hands.size - 1} -> ${target.hands.size} ))")
                    println("$name attacked ${newTarget.name} instead.")

                    // User gets back the attack card and plays it on the new target
                    pile.remove(attackCard)
                    hands.add(attackCard)
                    haveAttacked = false
                    attacked = false
                    val attackRangeCopy = attackRange
                    // Reset attack range to 999 to allow attacking any target
                    attackRangeOffset = 999
                    println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n")
                    attack(newTarget)
                    // Reset attack range back to original value
                    attackRange = attackRangeCopy
                    return false
                }
            }

            var dodgeBoolean = true

            if (this is MaChao) { // Ma Chao second ability
                val judgementCard = deck.first().suit
                if (this.hands.any { it.suit == judgementCard && it !is DodgeCard }) {
                    if (target.hands.any { it.suit == judgementCard && it !is DodgeCard } && target.useDodgeCard(target)) {
                        println("${target.name} has a card with the same suit as the top card of the deck.")
                        val card = target.hands.first { it.suit == judgementCard && it !is DodgeCard }
                        pile.add(deck.removeFirst())
                        target.hands.remove(card)
                        println("${target.name} discards ${card.name} (${card.suit}) from his hand.")
                        pile.add(card)
                    } else {
                        println("${target.name} doesn't have a card with the same suit as the top card of the deck.")
                        target.takeDamage(this, AttackCard(), 1)
                        dodgeBoolean = false
                    }
                }
            }

            if (dodgeBoolean) {
                dodgeBoolean = dodge(target)
            }
            target.blueSteelSword_effect = false

            if (!dodgeBoolean && equipmentList[0] is KirinBow && (target.equipmentList[2] != null || target.equipmentList[3] != null)) {
                println("< Kirin Bow \uD83C\uDFF9 >: Effect Activated!")
                println("< Kirin Bow \uD83C\uDFF9 >: Whenever your “Attack” deals damage to a target hero, you may discard one of his equipped mounts.")
                if (target.equipmentList[2] != null) {
                    // remove the mount
                    val mount1 = target.equipmentList[2] as MountCard
                    mount1.removeEquipment(target)
                    println("$name discarded ${target.name}'s mount ${mount1.name}")
                } else if (target.equipmentList[3] != null) {
                    // remove the mount
                    val mount2 = target.equipmentList[3] as MountCard
                    mount2.removeEquipment(target)
                    println("$name discarded ${target.name}'s mount ${mount2.name}")
                } else {
                    println("${target.name} has no mount equipped.")
                }
            }

            if (dodgeBoolean && hands.size >= 2 && equipmentList[0] is RockCleavingAxe) {
                println("<Rock Cleaving Axe \uD83E\uDE93 >: Effect Activated!")
                println("<Rock Cleaving Axe \uD83E\uDE93 >: When your “Attack” is cancelled by a “Dodge” play by target hero, you may discard two cards from your hand or equipment zone. If you do, ignore that “Dodge” and deals damage to that hero.")
                // Remove 2 cards in hands by chooseCard()
                val card1 = chooseCard(this, hands)
                val card2 = chooseCard(this, hands)
                println("-> $name discarded ${card1.name} and ${card2.name}. (Hand size: ${hands.size} -> ${hands.size - 2})")
                hands.remove(card1)
                hands.remove(card2)
                pile.add(card1)
                pile.add(card2)
                println("${target.name} took the damage from <Rock Cleaving Axe>.")
                target.takeDamage(this, AttackCard(), 1)
            }

            // If the player has ZhugeCrossbow, then he can attack again
            haveAttacked = if (equipmentList[0] is ZhugeCrossbow) {
                println("< Zhuge Crossbow \uD83C\uDFF9 >: Effect Activated!")
                println("$name can attack again!")
                false
            } else {
                true
            }
            attacked = true

            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n")
        } else if (equipmentList[0] is SerpentSpear && hands.size >= 2) {
            println("< Serpent Spear \uD83D\uDDE1️ >: Effect Activated!")
            println("< Serpent Spear \uD83D\uDDE1️ >: Whenever you need to play or discard an “Attack”, you may discard 2 cards from your hand instead")
            println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ATTACK !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            println("$name attacked ${target.name}")
            dodge(target)
            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            haveAttacked = true
            return true
        } else {
            println("$name has no attack card in hand...")
            return false
        }
        return true
    }

    fun canAttackEnemy(enemy: Character): Boolean {
//        if(getClosestEnemy() != enemy)
//            println("############### ERROR ###############")
        getEnemyDistance()

        val weaponCard = equipmentList[0] as? WeaponCard
        val mountCard = equipmentList[2] as? MountCard

        // If there is no weapon, then the range is 1 + mount range
        // If there is a weapon, then the range is weapon range + mount range
        val mountRange = if (mountCard == null) 0 else kotlin.math.abs(mountCard.range)
        val weaponRange = weaponCard?.attackRange ?: 1

        debug("- weaponRange: $weaponRange, mountRange: $mountRange")

        // Calculate the attack range
        val attackRange = weaponRange + kotlin.math.abs(mountRange) + attackRangeOffset

        // Check if the enemy is within attack range
        // If enemy has a mount in equipmentList[3], then the distance is distance in EnemyDistacne + mount range
        // If enemy has no mount, then the distance is distance in EnemyDistance

        val enemyDistance = if (enemy.equipmentList[3] is MountCard) {
            enemyDistance[enemy]?.plus(kotlin.math.abs((enemy.equipmentList[3] as MountCard).range))
                ?.plus(enemy.distanceOffset)
        } else {
            enemyDistance[enemy]?.plus(enemy.distanceOffset)
        }

        debug("- Attack Range: $attackRange VS Enemy Distance: $enemyDistance")

        return if (enemyDistance != null) {
            attackRange >= enemyDistance
        } else {
            // If enemyDistance is null, then the enemy is not in the game
            false
        }
    }

    fun dodge(target: Character): Boolean {
        // normal dodge is true if the target is not Lu Bu
        var normalDodge = true
        debug("target.attacker: ${target.attacker?.name}")

        if (target.attacker is LuBu) {
            println("Lu Bu's ability || Matchless ||:")
            println("\tDefending player need to respond with 2 “Dodge” instead of one to cancel")

            println("First time to dodge: ")

            // If the firstDodge is false, then no need to dodge again (since It has already dealt damage)
            // In the other words, if target dodge successfully, then he has to dodge again (due to Lu Bu's ability)
            normalDodge = useDodgeCard(target)
            if (normalDodge)
                println("\nSecond time to dodge: ")
        }

        // If the target has a dodge card in hand, then he can dodge the attack
        if (useDodgeCard(target) && normalDodge) {
            return true
        } else {
            println("${target.name} is unable to dodge attack")

            // Frost Blade
            if (equipmentList[0] is FrostBlade && target.hands.size >= 2 && target.hp >= 2) {
                println("<Frost Blade>: Effect Activated!")
                println("<Frost Blade>: Whenever your “Attack’ deals damage to a hero, you may prevent that damage. If you do, choose 2 cards in that hero’s hand or equipment zone and discard them.")
                debug("-> ${target.name} has ${target.hands.size} cards in hand and ${target.equipmentList.size} cards in equipment zone")
                var count = 0
                while (count < 2) {
                    val equipment = getEquippedEquipment(target)
                    if (equipment != null) {
                        println("-> $name discarded ${equipment.name} from ${target.name}'s equipment zone")
                        val equipmentTmp = equipment as EquipmentCard
                        equipmentTmp.removeEquipment(target)
                    } else {
                        val card = chooseCard(target, target.hands)
                        println("-> $name discarded ${card.name} from ${target.name}'s hand")
                        target.hands.remove(card)
                        pile.add(card)
                    }
                    count++
                }
            }

            target.takeDamage(this, AttackCard(), 1)
            return false
        }
    }

    fun getEquippedEquipment(character: Character): Card? {
        for (equipment in character.equipmentList)
            if (equipment != null)
                return equipment
        return null
    }

    open fun useCard(card: Card) {
        hands.remove(card)
        pile.add(card)
    }

    open fun getEnemyDistance() {
        enemyDistance.clear()

        val aliveCount = character.count { !it.isDead }
        debug("Alive Count: $aliveCount")
        debug("Alive Character: ${character.filter { !it.isDead }.joinToString { "[" + it.name + "]" }}")

        for (hero in character) {
            if (role.getEnemyList().contains(hero.role.roleTitle))
                if (!hero.isDead)
                    enemyDistance[hero] = findDistance(hero, aliveCount)
        }
    }

    open fun getAliveCharacter(): List<Character> {
        return character.filter { !it.isDead }
    }

    open fun judgmentPhase() {
        println("* Judgment Phrase")
        if (this.dTacticsZone.isEmpty()) {
            return
        }
        println("\t${name}'s Delay Tactics Zone: ${dTacticsZone.joinToString { "[" + it.name + "] " }}")
        val dTacticsCopy = mutableListOf<DelayTacticsCard>()
        dTacticsCopy.addAll(dTacticsZone)
        for (dTactic in dTacticsCopy) {
            if (!isDead)
                dTactic.check(this)
        }
    }

    open fun drawPhase() {
        turnEnd = false
        if (isDead || !checkWin())
            return

        if (this is MaChao) {
            attackRangeOffset = 1
        }

        println("* Draw Phrase")
        drawTwoCards()
        // Change haveAttacked's value to false
        // -> Now this part is changed to the end of the turn
    }

    open fun discardPhase() {
        if (isDead || !checkWin())
            return

        println("* Discard Phrase")
        if (hands.size <= hp) {
            println("\t${name}'s HP is ${hp}, number of cards is ${hands.size}. No need to discard cards.")
            return
        }

        //!!!: The method below SIMPLY RANDOMLY discard hands <- Selfish Algo.
        while (hands.size > hp && hp > 0 + handSizeOffset) {
            println("\t${name}'s HP is ${hp}, now have ${hands.size} cards.")
            discard()
        }

        println("\t${name}'s HP is ${hp}, now have ${hands.size} cards.")
    }

    open fun useTacticCard() {
        val tacticsCardsInHand = hands.filterIsInstance<TacticsCard>()

        val harvestCard = tacticsCardsInHand.find { it is Harvest }
        if (harvestCard != null) {
            hands.remove(harvestCard)
            pile.add(harvestCard)
            println("$name is using Tactic Card ${harvestCard.name} ( Hands:　${hands.size + 1}　-> ${hands.size} )")
            for (target in getAliveCharacter()) {
                harvestCard.effect(this, target)
            }
            if (this is HuangYueYing) {
                this.abilityList.first().use(this, null)
            }
        } else {
            for (card in getTacticCards()) {
                val matchingCard = tacticsCardsInHand.find { it.name == card.name }
                if (matchingCard != null) {
                    println("$name is using Tactic Card ${card.name} ( Hands:　${hands.size + 1}　-> ${hands.size} )")
                    val closestEnemy = getClosestEnemy()
                    if (closestEnemy != null) {
                        // Check if the closest enemy has the ImpeccablePlan card in their hand
                        val hasImpeccablePlan = closestEnemy.hands.any { it is ImpeccablePlan }
                        if (hasImpeccablePlan) {
                            closestEnemy.hands.removeIf { it is ImpeccablePlan }
                            pile.add(ImpeccablePlan())
                            hands.remove(matchingCard)
                            pile.add(matchingCard)
                            println("${closestEnemy.name} uses the Impeccable Plan to block the ${matchingCard.name}. ( Hands:　${closestEnemy.hands.size + 1}　-> ${closestEnemy.hands.size} )")
                            if (this is HuangYueYing) {
                                this.abilityList.first().use(this, null)
                            }
                            return
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
                    if (this is HuangYueYing) {
                        this.abilityList.first().use(this, null)
                    }
                }
            }
        }

    }

    open fun useAbility() {
        if (checkWin())
            return
    }

    open fun sortingHand() {
        hands.sortBy {
            when (it) {
                is AttackCard -> 0
                is ImpeccablePlan -> hands.size - 2
                is PeachCard -> hands.size - 1
                is DodgeCard -> hands.size // sort DodgeCard last
                else -> 1 // keep the order for unknown card types
            }
        }
    }

    open fun discard() {
        sortingHand()
        if (basicStrategy.state is HealthyState) {
            val dCard = hands.last()
            hands.remove(dCard)
            pile.add(dCard)
            println("$name discards ${dCard.name}. (Hand size: ${hands.size + 1} -> ${hands.size})")
        } else {
            val dCard = hands.first()
            hands.remove(dCard)
            pile.add(dCard)
            println("$name discards ${dCard.name}. (Hand size: ${hands.size + 1} -> ${hands.size})")
        }
    }

    open fun endPhase() {
        if (isDead || !checkWin())
            return

        if ((character.any { it.name == "Lu Xun" })) {
            println("Put all his hero cards back to his hand. ( Hand: 0 -> ${this.hands.size} )")
        }

        debug("Deck size: ${deck.size} $library")
        debug("Pile size: ${pile.size}")
        if (deck.size <= 10) {
            library.reFill()
        }
        turnEnd = true
        haveAttacked = false
        isAbandon = false
        blueSteelSword_effect = false
        if (this !is MaChao) {
            attackRangeOffset = 0
        }
        dealDamageOffset = 0
        distanceOffset = 0
        handSizeOffset = 0
    }

    open fun dodgeEffectHandle() {

    }
}


