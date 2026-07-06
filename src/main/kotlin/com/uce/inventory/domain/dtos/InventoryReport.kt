package com.uce.inventory.domain.dtos

data class InventoryReport(
  val totalProducts: Int,
  val activeCategories: Set<String>,
  val stockByCategory: Map<String, Int>
)