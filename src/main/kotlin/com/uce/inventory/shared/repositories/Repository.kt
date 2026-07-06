package com.uce.inventory.shared.repositories

import com.uce.inventory.shared.errors.RepositoryResult

interface Repository<T, ID> {
  suspend fun findById(id: ID): RepositoryResult<T>

  suspend fun findAll(): List<T>

  suspend fun save(item: T): RepositoryResult<T>

  suspend fun delete(id: ID): RepositoryResult<Unit>

  suspend fun update(item: T): RepositoryResult<T>
}