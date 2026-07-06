package com.uce.inventory.infrastructure.repositories

import com.uce.inventory.domain.errors.InventoryError
import com.uce.inventory.domain.models.Transaction
import com.uce.inventory.domain.repositories.TransactionRepository
import com.uce.inventory.shared.errors.RepositoryResult
import com.uce.inventory.shared.sequences.idSequenceGen

class TransactionRepositoryImpl(private var transactions: List<Transaction> = listOf()) : TransactionRepository {

  private val idGen: Iterator<Long> = idSequenceGen().iterator()

  override suspend fun findById(id: Long): RepositoryResult<Transaction> {
    val transaction = transactions.find { it.id == id }

    if (transaction == null) return RepositoryResult.Failure(InventoryError.ItemNotFound)

    return RepositoryResult.Success(transaction)
  }

  override suspend fun findAll(): List<Transaction> {
    return this.transactions
  }

  override suspend fun save(item: Transaction): RepositoryResult<Transaction> {
    val nextId = idGen.next()

    val newTransaction = item.copy(id = nextId)

    transactions = transactions.plus(newTransaction)

    return RepositoryResult.Success(newTransaction)
  }

  override suspend fun delete(id: Long): RepositoryResult<Unit> {
    val exists = transactions.any { it.id == id }

    if (!exists) return RepositoryResult.Failure(InventoryError.ItemNotFound)

    transactions = transactions.filterNot { it.id == id }

    return RepositoryResult.Success(Unit)
  }

  override suspend fun update(item: Transaction): RepositoryResult<Transaction> {
    val exists = transactions.any { it.id == item.id }

    if (!exists) return RepositoryResult.Failure(InventoryError.ItemNotFound)

    transactions = transactions.map { if (it.id == item.id) item else it }

    return RepositoryResult.Success(item)
  }


}