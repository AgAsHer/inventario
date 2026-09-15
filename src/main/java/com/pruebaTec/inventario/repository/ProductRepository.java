package com.pruebaTec.inventario.repository;

import com.pruebaTec.inventario.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository 

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

@Query(value = "SELECT validar_sku_unico(:sku)", nativeQuery = true)
boolean validarSkuUnico(@Param("sku") String sku);

@Query(value = "SELECT actualizar_stock(:productId, :delta)", nativeQuery = true)
Object actualizarStock(@Param("productId") Long productId, @Param("delta") Integer delta);
}
