package pansong291.piano.wizard.utils

object MusicUtil {
    // 0 1 2 3 4 5 6 7 8 9 10 11
    // 0   2   4 5   7   9    11
    private val semitones = listOf(1, 3, 6, 8, 10)
    private val naturals = listOf(0, 2, 4, 5, 7, 9, 11)

    /**
     * 判断是否是半音（钢琴黑键）
     * @param tet12 十二平均律
     */
    fun isSemitone(tet12: Int): Boolean {
        return semitones.contains(tet12 % 12)
    }

    /**
     * 判断是否是自然音（钢琴白键）
     * @param tet12 十二平均律
     */
    fun isNatural(tet12: Int): Boolean {
        return naturals.contains(tet12 % 12)
    }

    /**
     * 基本音级转十二平均律
     * @param basic 基本音级 0~6
     * @return 十二平均律 0~11
     */
    fun basicNoteTo12TET(basic: Int): Int {
        var tet12 = basic * 2
        if (tet12 >= 6) tet12--
        return tet12
    }
}
