package com.uce.inventory.shared.utils

import com.uce.inventory.domain.errors.InventoryError


fun mapInventoryError(error: InventoryError): String {
  return when (error) {
    InventoryError.ItemNotFound -> "Item not found"
    InventoryError.DuplicateItem -> "Item duplicate"
    is InventoryError.InsufficientStock -> "Insufficient stock"
  }
}