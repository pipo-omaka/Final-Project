package com.example

import kotlinx.serialization.Serializable




@Serializable
data class PollWithOptionsResponse(
    val poll: Poll,
    val options: List<PollOption>
)
