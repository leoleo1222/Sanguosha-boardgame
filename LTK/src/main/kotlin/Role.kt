interface Strategy {

}
class HealthyState : State {

    private lateinit var s: BasicStrategy

    fun setStrategy(strategy: BasicStrategy) {
        s = strategy
    }

    companion object {
        fun create(strategy: BasicStrategy): HealthyState {
            val healthyState = HealthyState()
            healthyState.setStrategy(strategy)
            return healthyState
        }
    }
}

class UnhealthyState : State {

    private lateinit var s: BasicStrategy

    fun setStrategy(strategy: BasicStrategy) {
        s = strategy
    }

    companion object {
        fun create(strategy: BasicStrategy): UnhealthyState {
            val unhealthyState = UnhealthyState()
            unhealthyState.setStrategy(strategy)
            return unhealthyState
        }
    }

}

class BasicStrategy(private val character: Character) : Strategy {
    var state: State = HealthyState.create(this)

    val recommend: String
        get() {
            return when (state) {
                is HealthyState -> "Keep peach card (hp :${character.hp})"
                is UnhealthyState -> "Use peach card (hp :${character.hp})"
                else -> ""
            }
        }

    fun changeState(state: State) {
        this.state = state
    }

}



interface State {

}

interface Role {
    var roleTitle: String
    fun getEnemy(): String
    fun getEnemyList(): MutableList<String>
}

class Monarch : Role {
    override var roleTitle: String = "Monarch"
    override fun getEnemy(): String {
        return "Rebel, then Traitors."
    }

    override fun getEnemyList(): MutableList<String> {
        return mutableListOf("Rebel", "Traitors")
    }
}

class Minister : Role {
    override var roleTitle: String = "Minister"
    override fun getEnemy(): String {
        return "Rebel, then Traitors."
    }

    override fun getEnemyList(): MutableList<String> {
        return mutableListOf("Rebel", "Traitors")
    }
}

class Traitors : Role {
    override var roleTitle: String = "Traitors"
    override fun getEnemy(): String {
        return "attack Rebel, then Monarch."
    }

    override fun getEnemyList(): MutableList<String> {
        return mutableListOf("Rebel", "Monarch")
    }
}

class Rebel : Role {
    override var roleTitle = "Rebel"
    override fun getEnemy(): String {
        return "Monarch, then Minister."
    }

    override fun getEnemyList(): MutableList<String> {
        return mutableListOf("Monarch", "Minister")
    }
}

abstract class MonarchHero(r: Monarch) : Character(r) {
    override var maxHP = 5
    override var hp = 5
}

abstract class WarriorHero(r: Role) : Character(r) {
    override var maxHP = 4
    override var hp = 4
}

abstract class AdvisorHero(r: Role) : Character(r) {
    override var maxHP = 3
    override var hp = 3
}

interface CaoCaoHandler {
    fun setNext(h: CaoCaoHandler)
    fun handle(): Boolean
}

abstract class WeiHero(r: Role) : Character(r), CaoCaoHandler {
    open var nextHero: CaoCaoHandler? = null
    override fun setNext(h: CaoCaoHandler) {
        nextHero = h
    }

    override fun handle(): Boolean {
        if (!isDead) {
            if (roleTitle != "Rebel") {
                return if (hands.filterIsInstance<DodgeCard>().isNotEmpty()) {
                    val dodgeCard = hands.filterIsInstance<DodgeCard>().first()
                    println("$name spent 1 card to help his/her lord to dodge.")
                    hands.remove(dodgeCard)
                    //
                    true
                } else {
                    if (nextHero != null) {
                        println("$name doesn't want to help.")
                        nextHero!!.handle()
                    } else {
                        println("No one can help lord to dodge.")
                        false
                    }
                }
            } else {
                println("$name doesn't want to help.")
                return if (nextHero != null) {
                    nextHero!!.handle()
                } else {
                    println("No one can help lord to dodge.")
                    false
                }
            }
        } else {
            if (nextHero != null) {
                return nextHero!!.handle()
            }
        }
        return false
    }
}

interface LiuBeiHandler {
    fun setNext(h: LiuBeiHandler)
    fun LiuBeiHandle(attack: Character?): Boolean

    fun getNext(): LiuBeiHandler?
}

abstract class ShuHero(r: Role) : Character(r), LiuBeiHandler {
    open var nextHero: LiuBeiHandler? = null
    override fun setNext(h: LiuBeiHandler) {
        nextHero = h
    }

    override fun getNext(): LiuBeiHandler? {
        return nextHero
    }

    override fun LiuBeiHandle(attack: Character?): Boolean {
        // If the ShuHero is dead, call nextHero to handle the attack
        if (isDead) return nextHero?.LiuBeiHandle(attack) ?: false
        // If the ShuHero has an attack card, use it to attack the target
        return if (useAttackCard()) {
            // Filter the hands to get only AttackCards
            val attackCards = hands.filterIsInstance<AttackCard>()
            // Choose a card to play
            val attackCard = chooseCard(this, attackCards)
            // Play the chosen card
            playCard(attackCard, attack)
            // Print the message "Rouse!!!!!!!!!!!"
            println("Rouse!!!!!!!!!!!")
            // If the target is not null, print a message to indicate that the ShuHero attacked the target
            if (attack != null) {
                println("$name ( HP: $hp ) attacked \uD83D\uDDE1 ${attack.name} ( HP: ${attack.hp} ) for LiuBei")
            }
            true
        } else { // If the ShuHero doesn't have an attack card, call nextHero to handle the attack
            println("$name does not have attack card")
            nextHero?.LiuBeiHandle(attack) ?: false
        }
    }

}

interface SunQuanHandler {
    fun setNext(h: SunQuanHandler)
    fun SunQuanHandle(protect: SunQuan): Boolean
}

abstract class WuHero(r: Role) : Character(r), SunQuanHandler {
    open var nextHero: SunQuanHandler? = null
    override fun setNext(h: SunQuanHandler) {
        nextHero = h
    }

    override fun SunQuanHandle(protect: SunQuan): Boolean {
        if (!isDead) {
            if (hands.filterIsInstance<PeachCard>().isNotEmpty()) {
                if (hp > protect.hp && protect.hp != protect.maxHP && protect.hp <= 0) {
                    protect.hp += 1
                    println("$name [$roleTitle] want to spent 1 peach card to heal him/her lord ${protect.name} (HP: ${protect.hp - 1} -> ${protect.hp})")
                    hands.remove(hands.filterIsInstance<PeachCard>().first())
                    hands.add(deck.removeFirst())
                    println("$name draw one card !!! [${hands.last()}]")
                    return true
                }
            } else {
                return if (nextHero != null) {
                    println("$name no peach card to help him/her lord.")
                    nextHero!!.SunQuanHandle(protect)
                } else {
                    println("No one can help lord to heal.")
                    return false
                }
            }
        } else {
            if (nextHero != null) {
                return nextHero!!.SunQuanHandle(protect)
            }
        }
        return false
    }
}

// how we can use this class
//val monarch = Role.createMonarch()
//val minister = Role.createMinister()
//val rebel = Role.createRebel()
//val traitor = Role.createTraitor()
//
//println(monarch.name) // prints "Monarch"
//println(minister.victoryCondition) // prints "Protect the monarch no matter the cost."


