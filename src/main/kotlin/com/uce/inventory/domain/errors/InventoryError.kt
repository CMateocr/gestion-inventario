package com.uce.inventory.domain.errors

sealed class InventoryError {
  object DuplicateItem : InventoryError()
  object ItemNotFound : InventoryError()

  data class InsufficientStock(val missing: Int) : InventoryError()
}