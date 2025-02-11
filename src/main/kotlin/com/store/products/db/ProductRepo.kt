package com.store.products.db

import com.store.products.ProductDetails
import com.store.products.ProductType
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface ProductRepo {
    fun findAll(type: ProductType?): List<ProductDetails>

    fun save(product: ProductDetails): Int
}

@Repository
class InMemoryProductDb : ProductRepo {
    private var products = ConcurrentHashMap<Int, ProductDetails>()
    override fun findAll(type: ProductType?): List<ProductDetails> = products.values.filter { it.type == type }.toList()

    override fun save(product: ProductDetails): Int {
        products.putIfAbsent(products.size + 1, product)
        return products.size
    }
}
