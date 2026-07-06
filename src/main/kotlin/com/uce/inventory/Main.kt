package com.uce.inventory

import com.uce.inventory.application.usecases.InventoryUseCase
import com.uce.inventory.domain.models.Product
import com.uce.inventory.domain.models.Transaction
import com.uce.inventory.infrastructure.repositories.ProductRepositoryImpl
import com.uce.inventory.infrastructure.repositories.TransactionRepositoryImpl
import com.uce.inventory.shared.enums.Category
import com.uce.inventory.shared.enums.TransactionType
import com.uce.inventory.shared.errors.RepositoryResult
import com.uce.inventory.shared.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

fun main() = runBlocking {
  val loadingJob = launch {
    print("Iniciando motor de base de datos en memoria")
    for (i in 1..4) {
      delay(400)
      print(".")
    }
    println("\n[SISTEMA] Conexion establecida con exito. Cargando modulos...")
    delay(600)
  }

  loadingJob.join()

  val productRepository = ProductRepositoryImpl()
  val transactionRepository = TransactionRepositoryImpl()
  val inventoryUseCase = InventoryUseCase(productRepository, transactionRepository)

  var isRunning = true

  println("=========================================")
  println("      SISTEMA DE INVENTARIO UCE          ")
  println("=========================================")

  while (isRunning) {
    println("\n--- MENU PRINCIPAL ---")
    println("1. Agregar Producto al Catalogo")
    println("2. Ver Productos del Catalogo")
    println("3. Actualizar Producto")
    println("4. Eliminar Producto")
    println("5. Registrar Ingreso de Stock")
    println("6. Registrar Salida de Stock")
    println("7. Ver Reporte Funcional y Valorizacion")
    println("8. Salir")
    print("Opcion: ")

    when (readlnOrNull()) {
      "1" -> {
        println("\n--- NUEVO PRODUCTO ---")
        print("Ingrese ID numerico: ")
        val id = readlnOrNull()?.toLongOrNull()

        if (id == null) {
          println("[ERROR] ID invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese nombre del producto: ")
        val name = readlnOrNull() ?: "Sin nombre"

        print("Ingrese precio base: ")
        val price = readlnOrNull()?.toDoubleOrNull()

        if (price == null || price < 0) {
          println("[ERROR] Precio invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese categoria (ELECTRONICS, CLOTHING, FOOD, BOOKS, FURNITURE): ")
        val categoryInput = readlnOrNull()?.uppercase() ?: ""

        val category = Category.entries.find { it.name == categoryInput }
        if (category == null) {
          println("[ERROR] La categoria '$categoryInput' no es valida. Operacion cancelada.")
          continue
        }

        val newProduct = Product(id, name, price, category)

        when (val result = inventoryUseCase.saveProduct(newProduct)) {
          is RepositoryResult.Success -> println("[EXITO] Producto guardado: ${result.data.name}")
          is RepositoryResult.Failure -> println("[ERROR] ${mapInventoryError(result.error)}")
        }
      }

      "2" -> {
        println("\n--- CATALOGO DE PRODUCTOS ---")
        val products = inventoryUseCase.findAllProducts()

        if (products.isEmpty()) {
          println("[INFO] El catalogo esta vacio.")
        } else {
          products.forEach { product ->
            println("[ID: ${product.id}] ${product.name} | Categoria: ${product.category} | Precio: ${roundValue(product.price)}")
          }
        }
      }

      "3" -> {
        println("\n--- ACTUALIZAR PRODUCTO ---")
        print("Ingrese el ID numerico del producto a actualizar: ")
        val id = readlnOrNull()?.toLongOrNull()

        if (id == null) {
          println("[ERROR] ID invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese NUEVO nombre del producto: ")
        val name = readlnOrNull() ?: "Sin nombre"

        print("Ingrese NUEVO precio base: ")
        val price = readlnOrNull()?.toDoubleOrNull()

        if (price == null || price < 0) {
          println("[ERROR] Precio invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese NUEVA categoria (ELECTRONICS, CLOTHING, FOOD, BOOKS, FURNITURE): ")
        val categoryInput = readlnOrNull()?.uppercase() ?: ""

        val category = Category.entries.find { it.name == categoryInput }
        if (category == null) {
          println("[ERROR] Categoria no valida. Operacion cancelada.")
          continue
        }

        val updatedProduct = Product(id, name, price, category)

        when (val result = inventoryUseCase.updateProduct(updatedProduct)) {
          is RepositoryResult.Success -> println("[EXITO] Producto actualizado correctamente.")
          is RepositoryResult.Failure -> println("[ERROR] ${mapInventoryError(result.error)}")
        }
      }

      "4" -> {
        println("\n--- ELIMINAR PRODUCTO ---")
        print("Ingrese el ID numerico del producto a eliminar: ")
        val id = readlnOrNull()?.toLongOrNull()

        if (id == null) {
          println("[ERROR] ID invalido. Operacion cancelada.")
          continue
        }

        when (val result = inventoryUseCase.deleteProduct(id)) {
          is RepositoryResult.Success -> println("[EXITO] Producto eliminado del catalogo.")
          is RepositoryResult.Failure -> println("[ERROR] ${mapInventoryError(result.error)}")
        }
      }

      "5" -> {
        println("\n--- INGRESO DE STOCK ---")
        print("Ingrese ID del producto: ")
        val productId = readlnOrNull()?.toLongOrNull()

        if (productId == null) {
          println("[ERROR] ID de producto invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese cantidad a ingresar: ")
        val quantity = readlnOrNull()?.toIntOrNull()

        if (quantity == null || quantity <= 0) {
          println("[ERROR] La cantidad debe ser un numero mayor a cero.")
          continue
        }

        val inputTx = Transaction(
          id = 0L,
          productId = productId,
          type = TransactionType.INCOME,
          quantity = quantity,
          date = LocalDate.now()
        )

        when (val result = inventoryUseCase.registerTransaction(inputTx)) {
          is RepositoryResult.Success -> println("[EXITO] Ingreso registrado. Ticket N: ${result.data.id}")
          is RepositoryResult.Failure -> println("[ERROR] ${mapInventoryError(result.error)}")
        }
      }

      "6" -> {
        println("\n--- SALIDA DE STOCK ---")
        print("Ingrese ID del producto: ")
        val productId = readlnOrNull()?.toLongOrNull()

        if (productId == null) {
          println("[ERROR] ID de producto invalido. Operacion cancelada.")
          continue
        }

        print("Ingrese cantidad a retirar: ")
        val quantity = readlnOrNull()?.toIntOrNull()

        if (quantity == null || quantity <= 0) {
          println("[ERROR] La cantidad debe ser un numero mayor a cero.")
          continue
        }

        val outputTx = Transaction(
          id = 0L,
          productId = productId,
          type = TransactionType.OUTCOME,
          quantity = quantity,
          date = LocalDate.now()
        )

        when (val result = inventoryUseCase.registerTransaction(outputTx)) {
          is RepositoryResult.Success -> println("[EXITO] Salida registrada. Ticket N: ${result.data.id}")
          is RepositoryResult.Failure -> println("[ERROR] ${mapInventoryError(result.error)}")
        }
      }

      "7" -> {
        println("\nProcesando colecciones asincronas...")
        val report = inventoryUseCase.generateInventoryReport()

        println("=========================================")
        println("  REPORTE DE INVENTARIO Y VALORIZACION   ")
        println("=========================================")
        println("Total de productos unicos: ${report.totalProducts}")
        println("Categorias activas: ${report.activeCategories.joinToString()}")

        println("\n--- Detalle por Producto ---")
        val products = inventoryUseCase.findAllProducts()

        if (products.isEmpty()) {
          println("No hay productos registrados para reportar.")
        } else {
          val totalCatalogBaseValue = sumInventoryBaseValueRecursive(products)
          println("Valor neto del catalogo: ${roundValue(totalCatalogBaseValue)}")

          products.forEach { product ->
            val stock = report.stockByCategory[product.category.toString()] ?: 0

            val priceWithIva = getPriceWithIVA(product.price, 0.15)
            val priceWithDiscount = applyDiscount(product.price, 0.10)
            val totalValue = getTotalPrice(product.price, stock)
            val stockState = getStockState(stock, 10)

            println("- [ID: ${product.id}] ${product.name}")
            println("   Categoria: ${product.category}")
            println("   Stock Actual: $stock unidades -> Estado: $stockState")
            println("   Precio Base: ${roundValue(product.price)}")
            println("   Precio Promocional (-10%): ${roundValue(priceWithDiscount)}")
            println("   Precio con IVA (15%): ${roundValue(priceWithIva)}")
            println("   Valorizacion Total en Stock: ${roundValue(totalValue)}")
          }
        }
      }

      "8" -> {
        println("Cerrando sistema...")
        isRunning = false
      }

      else -> println("[ADVERTENCIA] Opcion no valida. Intente nuevamente.")
    }
  }
}