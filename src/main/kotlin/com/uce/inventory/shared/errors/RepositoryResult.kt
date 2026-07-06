package com.uce.inventory.shared.errors

import com.uce.inventory.domain.errors.InventoryError

sealed class RepositoryResult<out T> {
  data class Success<T>(val data: T) : RepositoryResult<T>()

  data class Failure(val error: InventoryError) : RepositoryResult<Nothing>()
}