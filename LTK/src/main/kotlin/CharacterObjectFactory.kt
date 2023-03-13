import kotlin.random.Random

interface CharacterObjectFactory {
    fun getRandomRole(): Role
    fun createRandomHero(): Character
}

object MonarchFactory : CharacterObjectFactory {
    var hero: Character? = null
    var role: Role? = null
    override fun getRandomRole(): Role {
        role = Monarch()
        return Monarch()
    }

    val Monarch_Lits: MutableList<String> = mutableListOf("Liu Bei", "Cao Cao", "Sun Quan")
    override fun createRandomHero(): Character {
        val randomIndex = Random.nextInt(Monarch_Lits.size)
        val Monarch = when (Monarch_Lits[randomIndex]) {
            "Liu Bei" -> LiuBei(Monarch())
            "Cao Cao" -> CaoCao(Monarch())
            else -> SunQuan(Monarch())
        }
        Monarch_Lits.remove(Monarch_Lits[randomIndex])
        hero = Monarch
        return Monarch
    }
}

object NonMonarchFactory : CharacterObjectFactory {

    val NonMonarch_Role: MutableList<String> = mutableListOf("Minister", "Traitors", "Traitors", "Rebel")
    override fun getRandomRole(): Role {
        val randomIndex = Random.nextInt(NonMonarch_Role.size)
        val role = when (NonMonarch_Role[randomIndex]) {
            "Minister" -> Minister()
            "Traitors" -> Traitors()
            else -> Rebel()
        }
        NonMonarch_Role.remove(NonMonarch_Role[randomIndex])
        return role
    }

    val NonMonarch_Hero: MutableList<String> =
        mutableListOf(
            "Guan Yu",
            "Zhang Fei",
            "Zhuge Liang",
            "Zhang Yun",
            "Ma Chao",
            "Huang Yue Ying",
            "Gan Ning",
            "Lu Meng",
            "Huang Gai",
            "Zhou Yu",
            "Da Qiao",
            "Lu Xun",
            "Sun Shang Xiang",
            "Sima Yi",
            "Xiahou Dun",
            "Zhang Liao",
            "Xu Chu",
            "Guo Jia",
            "Zhen Ji",
            "Hua Tuo",
            "Lu Bu",
            "Diao Chan"
        )

    override fun createRandomHero(): Character {
        val randomIndex = Random.nextInt(NonMonarch_Hero.size)
        val NonMonarch = when (NonMonarch_Hero[randomIndex]) {
            "Guan Yu" -> GuanYu(getRandomRole())
            "Zhang Fei" -> ZhangFei(getRandomRole())
            "Zhuge Liang" -> ZhugeLiang(getRandomRole())
            "Zhang Yun" -> ZhangYun(getRandomRole())
            "Ma Chao" -> MaChao(getRandomRole())
            "Huang Yue Ying" -> HuangYueYing(getRandomRole())
            "Gan Ning" -> GanNing(getRandomRole())
            "Lu Meng" -> LuMeng(getRandomRole())
            "Huang Gai" -> HuangGai(getRandomRole())
            "Zhou Yu" -> ZhouYu(getRandomRole())
            "Da Qiao" -> DaQiao(getRandomRole())
            "Lu Xun" -> LuXun(getRandomRole())
            "Sun Shang Xiang" -> SunShangXiang(getRandomRole())
            "Sima Yi" -> SimaYi(getRandomRole())
            "Xiahou Dun" -> XiahouDun(getRandomRole())
            "Zhang Liao" -> ZhangLiao(getRandomRole())
            "Xu Chu" -> XuChu(getRandomRole())
            "Guo Jia" -> GuoJia(getRandomRole())
            "Zhen Ji" -> ZhenJi(getRandomRole())
            "Hua Tuo" -> HuaTuo(getRandomRole())
            "Lu Bu" -> LuBu(getRandomRole())
            else -> DiaoChan(getRandomRole())
        }

        val currentMonarch = MonarchFactory.hero
        when (NonMonarch) {
            is WeiHero -> {
                if (currentMonarch is CaoCao && NonMonarch.roleTitle == "Minister") {
                    if (currentMonarch.nextHandler == null) {
                        currentMonarch.nextHandler = NonMonarch
                    } else {
                        NonMonarch.nextHero = currentMonarch.nextHandler
                        currentMonarch.nextHandler = NonMonarch
                    }

                    debug("${NonMonarch.name} with ${NonMonarch.roleTitle} joined CaoCao's team")
                }
            }
            is ShuHero -> {
                if (currentMonarch is LiuBei && NonMonarch.roleTitle == "Minister") {
                    if (currentMonarch.nextHandler == null) {
                        currentMonarch.nextHandler = NonMonarch
                    } else {
                        NonMonarch.nextHero = currentMonarch.nextHandler
                        currentMonarch.nextHandler = NonMonarch
                    }
                    debug("${NonMonarch.name} with ${NonMonarch.roleTitle} joined LiuBei's team")
                }
            }
            is WuHero -> {
                if (currentMonarch is SunQuan && NonMonarch.roleTitle == "Minister") {
                    if (currentMonarch.nextHandler == null) {
                        currentMonarch.nextHandler = NonMonarch
                    } else {
                        NonMonarch.nextHero = currentMonarch.nextHandler
                        currentMonarch.nextHandler = NonMonarch
                    }
                    debug("${NonMonarch.name} with ${NonMonarch.roleTitle} joined SunQuan's team")
                }
            }
        }

        NonMonarch_Hero.remove(NonMonarch_Hero[randomIndex])
        return NonMonarch
    }
}