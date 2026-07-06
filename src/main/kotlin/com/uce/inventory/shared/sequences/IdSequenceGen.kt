package com.uce.inventory.shared.sequences

fun idSequenceGen(): Iterator<Long> = generateSequence(1L) { it + 1L }.iterator()