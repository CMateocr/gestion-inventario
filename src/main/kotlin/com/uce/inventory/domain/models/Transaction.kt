package com.uce.inventory.domain.models

import java.time.LocalDate
import com.uce.inventory.shared.enums.TransactionType

data class Transaction(
  val id: Long,
  val productId: Long,
  val type: TransactionType,
  val quantity: Int,
  val date: LocalDate
)