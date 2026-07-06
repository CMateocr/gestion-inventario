package com.uce.inventory.shared.utils

import com.uce.inventory.domain.models.Product
import kotlin.math.round

fun getPriceWithIVA(basePrice: Double, ivaDecimal: Double): Double =
  basePrice * (1 + ivaDecimal)

fun applyDiscount(price: Double, discountDecimal: Double): Double {
  val discount = price * discountDecimal

  return price - discount
}

fun getTotalPrice(price: Double, quantity: Int): Double =
  price * quantity

fun getStockState(currentQuantity: Int, limit: Int): String =

  if (currentQuantity <= limit) "CRITICAL" else "STABLE"

fun roundValue(value: Double): String {
  val roundedValue = round(value * 100) / 100

  return "$${roundedValue}"
}

tailrec fun sumInventoryBaseValueRecursive(
  products: List<Product>,
  currentIndex: Int = 0,
  accumulator: Double = 0.0
): Double {
  if (currentIndex >= products.size) return accumulator

  val newAccumulator = accumulator + products[currentIndex].price
  
  return sumInventoryBaseValueRecursive(products, currentIndex + 1, newAccumulator)
}