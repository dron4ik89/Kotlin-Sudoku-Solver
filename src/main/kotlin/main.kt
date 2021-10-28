fun main(args: Array<String>) {

    val longest = "900080003010030080300706002703000208001205900609000704200807001070020060500040007"

    println("Custom data:")
    println(longest)

    val start = System.currentTimeMillis()

    try {

        val board = Board(longest)
        val solver = Solver()
        val solved = solver.solve(board)

        println("Solved data:")
        println(solved)

    } catch (e: ExceptionInvalidBoard) {

        println("invalid input")

    } catch (e: Exception) {

        println("invalid grid")

    }

    println("Time: ${(System.currentTimeMillis() - start)} millis")
}