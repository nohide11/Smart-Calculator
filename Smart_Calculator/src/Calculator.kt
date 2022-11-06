package calculator

import java.math.BigInteger
import java.util.*
import kotlin.math.pow

class SumNum(private val str: String, private var map: MutableMap<String, BigInteger>) {

	private val operationPriorety = mapOf<String, Int>(
		Pair("(", 0),
		Pair("+", 1),
		Pair("-", 1),
		Pair("*", 2),
		Pair("/", 2),
		Pair("^", 3),
		Pair("~", 4))

	private fun printResult(stringForRes: String) {
		fun singsOper(a: BigInteger, b: BigInteger, sing: String): BigInteger {
			return when (sing) {
				"+" -> a + b
				"-" -> a - b
				"*" -> a * b
				"/" -> (a / b)
				else -> a
			}
		}
		var correctStr = stringForRes.replace("(--)+".toRegex(), " + ")
		correctStr = correctStr.replace(Regex(pattern = "[+]+"), " + ").replace("[-]+".toRegex(), " - ")
		correctStr = correctStr.replace(Regex(pattern = "[*]+"), " * ").replace("/+".toRegex(), " / ")
		correctStr = correctStr.replace(")", " ) ").replace("(", " ( ")
		val stack: Stack<String> = Stack()
		val newList = toPostfix(correctStr.split(" ").toMutableList())
		for (i in newList.indices) {
			val c = newList[i]
			if (c == " ") continue
			if (c.toBigIntegerOrNull() != null) {
				stack.push(c)
			} else if (stack.size >= 2) {
				val a = stack.pop()!!.toBigInteger()
				val b = stack.pop()!!.toBigInteger()
				val newNum = singsOper(b, a, c).toString()
				stack.push(newNum)
			} else if (stack.size == 1) {
				val b = stack.pop()!!.toBigInteger()
				val a: Int = 0
				val newNum = singsOper(a.toBigInteger(), b, c).toString()
				stack.push(newNum)
			}
		}
		val result = stack.pop()!!.toBigInteger()
		println(result)
	}


	fun toPostfix(stringToPost: MutableList<String>): MutableList<String> {
		var postfix = mutableListOf<String>()

		val stack = Stack<String>()

		for (i in stringToPost.indices) {

			val c = stringToPost[i]

			if (c.toBigIntegerOrNull() != null) {
				postfix += "$c"
			} else if (c == "(") stack.push(c)
			else if (c == ")") {
				while (stack.size > 0 && stack.peek() != "(")
					postfix += "${stack.pop()}"
				stack.pop()
			} else if (operationPriorety.containsKey(c)) {
				val op = c

				while (stack.size > 0 && ( operationPriorety[stack.peek()]!! >= operationPriorety[op]!!))
					postfix += "${stack.pop()}"
				stack.push(op)
			}
		}
		for (op in stack.indices) {
			postfix += "${stack.pop()}"
		}

		return postfix
	}

	private fun regexCheck(): Boolean {
		val regexUnknownCom = Regex(pattern = "/\\w*")

		val splitQually: MutableList<String>
		val newStr = str.replace(" ", "")

		var fromNum = newStr.replace("[+|[-]|*|/|(|)]".toRegex(), "")

		val howMany1 = str.filter { it == '(' }.length
		val howMany2 = str.filter { it == ')' }.length
		if (howMany1 != howMany2 || str.contains("[*|/][*|/]+".toRegex())){
			println("Invalid expression")
			return false
		}



		val splitStr = str.split(" ")

		if (regexUnknownCom.matches(str)) {
			println("Unknown command")
			return false
		}

		if (!".+=.+".toRegex().matches(str)) {

			var count = true
			fromNum.forEach {
				if (it.digitToIntOrNull() == null) {
					count = false
				}
			}
			if (count) {
				printResult(str)
				return false
			}
			if (splitStr.size == 1) {
				if (!"[a-zA-Z]+".toRegex().matches(str) && splitStr.size == 1) {
					println("Invalid identifier")
					return false
				}

				if (!map.containsKey(str) && splitStr.size == 1) {
					println("Unknown variable")
					return false
				}
			}

			val splitQually = newStr.split("[+]+|[-]+|[(]+|[)]+|[*]+|[/]+|\\d+".toRegex()).toMutableList().filter { it != "" }.toMutableList()
			var chek: Boolean = true
			splitQually.forEach {
				if (!map.containsKey(it)) chek = false
			}
			if (chek) {
				var Fstr: String = str.replace(splitQually[0], map[splitQually[0]].toString())!!
				for (i in 1 until splitQually.size) {
					Fstr = Fstr.replace(splitQually[i], map[splitQually[i]].toString())!!
				}
				printResult(Fstr)
				return false
			}

		} else if (".+=.+".toRegex().matches(str)) {

			splitQually = newStr.split("=+".toRegex()).toMutableList()

			if (splitQually.size != 2) {
				println("Invalid assignment")
				return false
			}


			return if (!"[a-zA-Z]+".toRegex().matches(splitQually[0])) {
				println("Invalid identifier")
				false
			} else if ("[-|+]*\\d+".toRegex().matches(splitQually[1])) {
				map[splitQually[0]] = splitQually[1].toBigInteger()
				false
			} else if (splitQually[1].toBigIntegerOrNull() == null && !map.containsKey(splitQually[1])) {
				println("Invalid assignment")
				false
			} else if (map.containsKey(splitQually[1])) {
				map[splitQually[0]] = map[splitQually[1]]!!
				false
			} else {
				true
			}
		} else  printResult(str)
		return false
	}

	fun check(): Boolean {
		when (str) {
			"/exit" -> {
				println("Bye !")
				return true
			}
			"/help" -> {
				println("The program calculates the sum of numbers")
				return false
			}
			"" -> return false
		}

		return regexCheck()
	}
}

fun main() {
	var exit = false
	val map = mutableMapOf<String, BigInteger>()

	while (!exit) {

		val scan = Scanner(System.`in`)
		val sum = SumNum(scan.nextLine(), map)
		exit = sum.check()
	}
}
