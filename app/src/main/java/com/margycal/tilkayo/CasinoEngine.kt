package com.margycal.tilkayo

internal const val CREDIT = 10_000L
internal const val ANGER_LIMIT = -20_000L
internal val BETS = listOf(500L, 2_000L, 5_000L, 10_000L)

internal data class CasinoWallet(val chips: Long = CREDIT, val loan: Long = 0) {
    val balance get() = chips - loan
    val angry get() = balance <= ANGER_LIMIT

    fun borrow(): CasinoWallet {
        require(loan <= 10 * CREDIT - CREDIT)
        return copy(chips = chips + CREDIT, loan = loan + CREDIT)
    }

    fun repay(): CasinoWallet {
        val amount = minOf(chips, loan)
        return copy(chips = chips - amount, loan = loan - amount)
    }

    fun spin(bet: Long, reels: List<Int>): CasinoRound {
        require(bet in BETS && chips >= bet)
        require(reels.size == 3 && reels.all { it in 0..5 })
        val multiplier = when (reels.distinct().size) {
            1 -> 6
            2 -> 2
            else -> 0
        }
        val payout = bet * multiplier
        return CasinoRound(copy(chips = chips - bet + payout), payout)
    }
}

internal data class CasinoRound(val wallet: CasinoWallet, val payout: Long)
