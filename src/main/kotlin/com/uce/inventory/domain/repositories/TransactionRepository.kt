package com.uce.inventory.domain.repositories

import com.uce.inventory.domain.models.Transaction
import com.uce.inventory.shared.repositories.Repository

interface TransactionRepository : Repository<Transaction, Long> {
}