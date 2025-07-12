package com.example

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Poll(val id: Int, val question: String)

@Serializable
data class PollOption(val id: Int, val text: String, val voteCount: Int, val pollId: Int)

@Serializable
data class PollRequest(val question: String)

@Serializable
data class OptionRequest(val text: String, val pollId: Int)

@Serializable
data class PollUpdateRequest(val question: String)

@Serializable
data class OptionUpdateRequest(val text: String)

object PollRepository {
    private val polls = mutableListOf<Poll>()
    private val options = mutableListOf<PollOption>()
    private var pollIdCounter = 1
    private var optionIdCounter = 1

    fun getAllPolls(): List<Poll> = polls

    fun getPollById(id: Int): Poll? = polls.find { it.id == id }

    fun createPoll(request: PollRequest): Poll {
        val poll = Poll(pollIdCounter++, request.question)
        polls.add(poll)
        return poll
    }

    fun updatePoll(id: Int, request: PollUpdateRequest): Boolean {
        val index = polls.indexOfFirst { it.id == id }
        if (index != -1) {
            polls[index] = polls[index].copy(question = request.question)
            return true
        }
        return false
    }

    fun deletePoll(id: Int): Boolean {
        options.removeIf { it.pollId == id }
        return polls.removeIf { it.id == id }
    }

    fun addOption(request: OptionRequest): PollOption? {
        // ✅ validation: pollId ต้องมีอยู่จริง
        if (polls.none { it.id == request.pollId }) return null
        val option = PollOption(optionIdCounter++, request.text, 0, request.pollId)
        options.add(option)
        return option
    }

    fun updateOption(id: Int, request: OptionUpdateRequest): Boolean {
        val index = options.indexOfFirst { it.id == id }
        if (index != -1) {
            options[index] = options[index].copy(text = request.text)
            return true
        }
        return false
    }

    fun voteOption(id: Int): Boolean {
        val index = options.indexOfFirst { it.id == id }
        if (index != -1) {
            options[index] = options[index].copy(voteCount = options[index].voteCount + 1)
            return true
        }
        return false
    }

    fun getOptionsByPollId(pollId: Int): List<PollOption> =
        options.filter { it.pollId == pollId }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("🗳️ Welcome to Poll API!")
        }

        // ✅ GET /polls
        get("/polls") {
            call.respond(PollRepository.getAllPolls())
        }

        // ✅ GET /polls/{id}
        get("/polls/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)
                return@get
            }

            val poll = PollRepository.getPollById(id)
            if (poll != null) {
                val options = PollRepository.getOptionsByPollId(id)
                call.respond(PollWithOptionsResponse(poll, options))
            } else {
                call.respondText("Poll not found", status = HttpStatusCode.NotFound)
            }
        }

        // ✅ POST /polls
        post("/polls") {
            val request = call.receive<PollRequest>()
            val poll = PollRepository.createPoll(request)
            call.respond(HttpStatusCode.Created, poll)
        }

        // ✅ PUT /polls/{id}
        put("/polls/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid Poll ID", status = HttpStatusCode.BadRequest)
                return@put
            }

            val request = call.receive<PollUpdateRequest>()
            if (PollRepository.updatePoll(id, request)) {
                call.respondText("Poll updated successfully")
            } else {
                call.respondText("Poll not found", status = HttpStatusCode.NotFound)
            }
        }

        // ✅ DELETE /polls/{id}
        delete("/polls/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null && PollRepository.deletePoll(id)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respondText("Poll not found", status = HttpStatusCode.NotFound)
            }
        }

        // ✅ POST /options
        post("/options") {
            val request = call.receive<OptionRequest>()
            val option = PollRepository.addOption(request)
            if (option != null) {
                call.respond(HttpStatusCode.Created, option)
            } else {
                call.respondText("Poll ID does not exist", status = HttpStatusCode.BadRequest)
            }
        }

        // ✅ PUT /options/{id}
        put("/options/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid Option ID", status = HttpStatusCode.BadRequest)
                return@put
            }

            val request = call.receive<OptionUpdateRequest>()
            if (PollRepository.updateOption(id, request)) {
                call.respondText("Option updated successfully")
            } else {
                call.respondText("Option not found", status = HttpStatusCode.NotFound)
            }
        }

        // ✅ POST /options/{id}/vote
        post("/options/{id}/vote") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id != null && PollRepository.voteOption(id)) {
                call.respondText("Voted successfully")
            } else {
                call.respondText("Option not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
