package com.uce.inventory.domain.repositories

import com.uce.inventory.domain.models.Product
import com.uce.inventory.shared.repositories.Repository

interface ProductRepository : Repository<Product, Long> {
}