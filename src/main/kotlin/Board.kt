class Board {

    var cells = Array(9) { IntArray(9) }
    var fixedCells = Array(9) { BooleanArray(9) }

    constructor(){}

    constructor(str: String) {

        val arr = str.toCharArray().map { it.toString().toInt() }

        for(i in arr.indices){

            val col = i % 9
            val row = i / 9

            val v = arr[i]
            cells[row][col] = v
            fixedCells[row][col] = (v != 0)
        }
    }

    private fun isSummaryOk(summary: IntArray): Boolean {
        // we shouldn't have a zero on the board
        if (summary[0] != 0) {
            return false
        }

        // and we shouldn't have duplicates or missing values
        for (i in 1..9) {
            if (summary[i] != 1) {
                return false
            }
        }
        return true
    }

    fun isSolved(): Boolean {
        //  validate rows
        for (row in 0..8) {
            // ten elements
            val summary = IntArray(10)

            for (col in 0..8){
                val value = cells[row][col]
                summary[value]++
            }

            if (!isSummaryOk(summary)) {
                return false
            }
        }

        //  validate columns
        for (col in 0..8) {
            // ten elements
            val summary = IntArray(10)

            for (row in 0..8) {
                val value = cells[row][col]
                summary[value]++
            }

            if (!isSummaryOk(summary)) {
                return false
            }
        }

        // validate quadrant
        for(i in 0..8 step 3){
            for(j in 0..8 step 3){
                if (!isQuadrantValid(i, j)) return false
            }
        }

        return true
    }

    private fun isQuadrantValid(startRow: Int, startCol: Int): Boolean {
        val summary = IntArray(10)
        for (i in startRow until startRow + 3) {
            for (j in startCol until startCol + 3) {
                val value = cells[i][j]
                summary[value]++
            }
        }
        return isSummaryOk(summary)
    }

    fun isPlayValid(row: Int, col: Int, n: Int): Boolean {

        //  validate rows
        for (r in 0..8) {
            if (cells[r][col] == n) {
                return false
            }
        }

        //  validate columns
        for (c in 0..8) {
            if (cells[row][c] == n) {
                return false
            }
        }

        //  validate 3x3 matrix
        val br = row / 3
        val bc = col / 3
        for (r in br * 3 until (br + 1) * 3) {
            for (c in bc * 3 until (bc + 1) * 3) {
                if (cells[r][c] == n) {
                    return false
                }
            }
        }
        return true
    }

    override fun toString() = cells.joinToString("") { row ->
        row.joinToString("") { col ->
            col.toString()
        }
    }
}