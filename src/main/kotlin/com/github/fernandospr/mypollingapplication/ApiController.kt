package com.github.fernandospr.mypollingapplication

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ApiController {

    private val list = mutableListOf<Response<Person>>()

    @PostMapping("/person")
    fun postPerson(@RequestBody person: Person): Response<Unit> {
        val code = "1234-5678"
        list.clear()
        list.addAll(
            listOf(
                Response(
                    Transaction(
                        code = code,
                        status = TransactionStatus.PENDING.name
                    )
                ),
                Response(
                    Transaction(
                        code = code,
                        status = TransactionStatus.PENDING.name
                    )
                ),
                Response(
                    Transaction(
                        code = code,
                        status = TransactionStatus.APPROVED.name
                    ),
                    person
                )
            )
        )

        return Response(
            Transaction(
                code = code,
                status = TransactionStatus.CREATED.name,
                retry = list.size,
                timeout = 3000
            )
        )
    }

    @GetMapping("/person/{code}/status")
    fun getPersonStatus(@PathVariable("code") code: String): ResponseEntity<Response<Person>> {
        return if (list.isNotEmpty())
            ResponseEntity.ok(list.removeFirst())
        else
            ResponseEntity.notFound().build()
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(
    val transaction: Transaction,
    val payload: T? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Transaction(
    val code: String,
    val status: String,
    val retry: Int? = null,
    val timeout: Long? = null
)

enum class TransactionStatus {
    CREATED, APPROVED, REJECTED, PENDING
}

data class Person(val name: String)