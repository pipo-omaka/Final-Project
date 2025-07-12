package com.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PollRepositoryTest {
    @Test
    fun `create poll should add to list`() {
        val poll = PollRepository.createPoll(PollRequest("Test question"))
        val allPolls = PollRepository.getPolls()
        assertTrue { allPolls.contains(poll) }
    }

    @Test
    fun `vote should increment voteCount`() {
        val poll = PollRepository.createPoll(PollRequest("Vote test"))
        val option = PollRepository.addOption(OptionRequest("Option A", poll.id))
        PollRepository.voteOption(option.id)
        val updated = PollRepository.getOptionsForPoll(poll.id).find { it.id == option.id }
        assertEquals(1, updated?.voteCount)
    }
}