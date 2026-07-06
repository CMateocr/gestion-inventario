package com.uce.inventory.domain.models

import com.uce.inventory.shared.enums.Category

data class Product(val id: Long, val name: String, val price: Double, val category: Category)
