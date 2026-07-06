package com.uce.inventory.infrastructure.repositories

import com.uce.inventory.domain.errors.InventoryError
import com.uce.inventory.domain.models.Product
import com.uce.inventory.domain.repositories.ProductRepository
import com.uce.inventory.shared.errors.RepositoryResult

class ProductRepositoryImpl(private var products: List<Product> = listOf()) : ProductRepository {
  override suspend fun findById(id: Long): RepositoryResult<Product> {
    val product = products.find { it.id == id }

    if (product == null) {
      return RepositoryResult.Failure(InventoryError.ItemNotFound)
    }

    return RepositoryResult.Success(product)
  }

  override suspend fun findAll(): List<Product> {
    return products
  }

  override suspend fun save(item: Product): RepositoryResult<Product> {
    if (products.any { it.id == item.id }) return RepositoryResult.Failure(InventoryError.DuplicateItem)

    products = products.plus(item)

    return RepositoryResult.Success(item)
  }

  override suspend fun delete(id: Long): RepositoryResult<Unit> {
    val product = products.find { it.id == id }

    if (product == null) {
      return RepositoryResult.Failure(InventoryError.ItemNotFound)
    }

    products = products.minus(product)

    return RepositoryResult.Success(Unit)
  }

  override suspend fun update(item: Product): RepositoryResult<Product> {
    val product = products.find { it.id == item.id }

    if (product == null) {
      return RepositoryResult.Failure(InventoryError.ItemNotFound)
    }

    products = products.map { if (it.id == item.id) item else it }

    return RepositoryResult.Success(item)
  }


}