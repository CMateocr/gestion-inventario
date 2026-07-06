package com.uce.inventory.application.usecases

import com.uce.inventory.domain.dtos.InventoryReport
import com.uce.inventory.domain.errors.InventoryError
import com.uce.inventory.domain.models.Product
import com.uce.inventory.domain.models.Transaction
import com.uce.inventory.domain.repositories.ProductRepository
import com.uce.inventory.domain.repositories.TransactionRepository
import com.uce.inventory.shared.enums.TransactionType
import com.uce.inventory.shared.errors.RepositoryResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class InventoryUseCase(
  private val _productRepository: ProductRepository,
  private val _transactionRepository: TransactionRepository
) {

  suspend fun findProductById(id: Long): RepositoryResult<Product> {
    return this._productRepository.findById(id)
  }

  suspend fun findAllProducts(): List<Product> {
    return this._productRepository.findAll()
  }

  suspend fun saveProduct(product: Product): RepositoryResult<Product> {
    return this._productRepository.save(product)
  }


  suspend fun updateProduct(product: Product): RepositoryResult<Product> {
    return this._productRepository.update(product)
  }

  suspend fun deleteProduct(id: Long): RepositoryResult<Unit> {
    return this._productRepository.delete(id)
  }

  suspend fun findTransactionById(id: Long): RepositoryResult<Transaction> {
    return this._transactionRepository.findById(id)
  }

  suspend fun findAllTransactions(): List<Transaction> {
    return this._transactionRepository.findAll()
  }

  suspend fun deleteTransaction(id: Long): RepositoryResult<Unit> {
    return this._transactionRepository.delete(id)
  }

  suspend fun registerTransaction(transaction: Transaction): RepositoryResult<Transaction> {
    val productResult = this._productRepository.findById(transaction.productId)

    if (productResult is RepositoryResult.Failure) {
      return productResult
    }

    if (transaction.type == TransactionType.OUTCOME) {
      val currentStock = calculateCurrentStock(transaction.productId)

      if (currentStock < transaction.quantity) {
        return RepositoryResult.Failure(InventoryError.InsufficientStock(currentStock))
      }
    }

    return this._transactionRepository.save(transaction)
  }

  suspend fun updateTransaction(transaction: Transaction): RepositoryResult<Transaction> {

    val existingTxResult = this._transactionRepository.findById(transaction.id)

    if (existingTxResult is RepositoryResult.Failure) {
      return existingTxResult
    }

    val productResult = this._productRepository.findById(transaction.productId)

    if (productResult is RepositoryResult.Failure) {
      return productResult
    }

    return this._transactionRepository.update(transaction)
  }


  suspend fun generateInventoryReport(): InventoryReport = coroutineScope {
    val deferredProducts = async { _productRepository.findAll() }

    val deferredTransactions = async { _transactionRepository.findAll() }

    val products = deferredProducts.await()

    val transactions = deferredTransactions.await()

    val activeCategories: Set<String> = products.map { it.category.toString() }.toSet()

    val stockByCategory: Map<String, Int> = products
      .groupBy { it.category.toString() }
      .mapValues { entry ->
        entry.value.sumOf { product ->

          transactions
            .filter { it.productId == product.id }
            .fold(0) { acc, tx ->
              if (tx.type == TransactionType.INCOME) acc + tx.quantity else acc - tx.quantity
            }
        }
      }

    InventoryReport(
      totalProducts = products.size,
      activeCategories = activeCategories,
      stockByCategory = stockByCategory
    )
  }

  private suspend fun calculateCurrentStock(productId: Long): Int {
    val transactions = this._transactionRepository.findAll()

    return transactions
      .filter { it.productId == productId }
      .fold(0) { accumulatedStock, tx ->
        when (tx.type) {
          TransactionType.INCOME -> accumulatedStock + tx.quantity
          TransactionType.OUTCOME -> accumulatedStock - tx.quantity
        }
      }
  }
}
