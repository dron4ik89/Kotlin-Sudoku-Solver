class Solver {

    fun solve(board: Board): Board? {

        board.run {

            for (index in cells.indices) {

                for (i in 0..cells[index].size - 2) {
                    for (j in i + 1 until cells[index].size) {

                        //  validate rows
                        if (cells[index][i] != 0 && cells[index][i] == cells[index][j])
                            throw ExceptionInvalidBoard()

                        //  validate columns
                        if (cells[i][index] != 0 && cells[i][index] == cells[j][index])
                            throw ExceptionInvalidBoard()
                    }
                }
            }

            //  validate 3x3 matrix
            for (x in 0..8 step 3) {
                for (y in 0..8 step 3) {
                    val block = IntArray(9)

                    for (i in 0..2) {
                        for (j in 0..2) {
                            block[i * 3 + j] = cells[x + i][y + j]
                        }
                    }

                    for (i in 0..block.size - 2) {
                        for (j in i + 1 until block.size) {

                            if (block[i] != 0 && block[i] == block[j])
                                throw ExceptionInvalidBoard()
                        }
                    }
                }
            }
        }

        // optimize board
        // reorder so that we have the sections with more hints in the upper section (reduces the
        // search space)
        val sections = scoreSections(board)
        val swapInstructions = HashMap<Int, Int>()

        var s = 0
        for (sortedSection in sections.sortedByDescending { it.score }) {
            // from ---> to
            swapInstructions[sortedSection.id] = s
            s++
        }
        val swappedBoard = swapSections(board, sections, swapInstructions)

        // solve using backtracking
        backtrack(swappedBoard)

        // validate that the board is solved
        return if (swappedBoard.isSolved()) {
            // restore original section order
            val swapBackInstructions = HashMap<Int, Int>()
            for ((key, value) in swapInstructions) {
                swapBackInstructions[value] = key
                s++
            }
            swapSections(swappedBoard, sections, swapBackInstructions)
        } else {
            null
        }
    }

    private fun scoreSections(board: Board): List<Section> {
        // calculate how many fixed values we have in the upper, middle and bottom sections
        var upperScore = 0
        var middleScore = 0
        var bottomScore = 0

        for (row in 0..8) {
            var score = 0

            for (col in 0..8) {
                if (board.fixedCells[row][col]) {
                    score++
                }
            }

            when {
                row < 3 -> upperScore += score
                row < 6 -> middleScore += score
                else -> bottomScore += score
            }
        }

        return listOf(
            Section(0, upperScore),
            Section(1, middleScore),
            Section(2, bottomScore)
        )
    }



    private fun backtrack(board: Board) {
        //  backtracing iterative version
        var row = 0
        var col = 0
        var num = 1

        //  if row = 9 then we have found a solution (matrix range is from 0 to 8)
        start@ while (row < 9) {
            if (!board.fixedCells[row][col]) {

                for (n in num..9) {
                    if (board.isPlayValid(row, col, n)) {
                        //  move forward
                        //  increment position and start searching from 1 again for the next value
                        board.cells[row][col] = n
                        col++
                        if (col > 8) {
                            col = 0
                            row++
                        }
                        num = 1
                        continue@start
                    }
                }

            } else {
                // increment poisition and start searching from 1 again for the next value
                col++
                if (col > 8) {
                    col = 0
                    row++
                }
                num = 1
                continue@start
            }

            //  if it arrives here then no value is valid for the cell, so we need to go back
            //  clean the current cell
            board.cells[row][col] = 0

            //  decrease position, we need to go back to the first non fixed cell
            while (true) {
                col--
                if (col < 0) {
                    col = 8
                    row--
                }
                if (!board.fixedCells[row][col]) {
                    break
                }
            }

            // increase the value of the cell in order to keep searching for the solution
            num = board.cells[row][col] + 1
        }
    }

    private fun swapSections(
        board: Board,
        sections: List<Section>,
        swapInstructions: HashMap<Int, Int>
    ): Board {
        val tmpBoard = Board()
        for (section in sections) {
            val fromSection = section.id
            val toSection = swapInstructions[fromSection]!!
            val rowFrom = fromSection * 3
            var rowTo = toSection * 3
            for (i in rowFrom until rowFrom + 3) {
                for (j in 0..8) {
                    tmpBoard.cells[rowTo][j] = board.cells[i][j]
                    tmpBoard.fixedCells[rowTo][j] = board.fixedCells[i][j]
                }
                rowTo++
            }
        }
        return tmpBoard
    }
}