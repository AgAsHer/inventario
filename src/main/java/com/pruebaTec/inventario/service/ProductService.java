package com.pruebaTec.inventario.service;

import com.pruebaTec.inventario.dto.ProductDTO;
import com.pruebaTec.inventario.model.Product;
import com.pruebaTec.inventario.repository.ProductRepository;
import com.pruebaTec.inventario.exception.ResourceNotFoundException;
import com.pruebaTec.inventario.exception.DuplicateSkuException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service 
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    /**
     * Obiene listado completo de productos registrados
     * @return lista de productos en formato DTO
     */
    public List<ProductDTO> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();

    }

    /**
     * Busca un producto por su id
     * @param id identificador del producto
     * @return el producto encontrado el formato DTO
     * @throws ResourceNotFoundException si no existe un producto con ese id
     */
    public ProductDTO getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con id " + id + " no existe"));
        return convertToDTO(product);
    }

    /**
     * Crea un nuevo producto, validando que el SKU no esté ya en uso antes de continouar
     * @param productDTO datos del producto que se quiere crear
     * @return el producto creado en formato DTO
     * @throws DuplicateSkuException si el SKU ya está en uso
     */
    @Transactional 
    public ProductDTO createProduct(ProductDTO productDTO) {
        boolean skuDisponible = productRepository.validarSkuUnico(productDTO.getSku());

        if (!skuDisponible) {
            throw new DuplicateSkuException("El SKU " + productDTO.getSku() + " ya está en uso");
        }

        Product product = new Product();
        product.setSku(productDTO.getSku());
        product.setNombre(productDTO.getNombre());
        product.setPrecio(productDTO.getPrecio());
        product.setCantidad(productDTO.getCantidad());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product productoGuardado = productRepository.save(product);

        return convertToDTO(productoGuardado);
    }

    /**
     * Actualiza los datos de un producto existente. Si el SKU cambia, valida
     * que el nuevo esté disponible. La cantidad se ajusta por medio de la 
     * función actualizarStock de Postgre, que valida que no quede en negativo.
     * @param id identificador del producto a actualizar
     * @param productDTO nuevos datos del producto
     * @return el producto actualizado en formato DTO
     * @throws ResourceNotFoundException si no existe un con ese id
     * @throws DuplicateSkuException si el nuevo SKU ya esta en uso
     */
    @Transactional 
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con id " + productDTO.getSku() + " no existe"));

        if (!product.getSku().equals(productDTO.getSku())) {
            boolean skuDisponible = productRepository.validarSkuUnico(productDTO.getSku());
            if (!skuDisponible) {
                throw new DuplicateSkuException("El SKU " + productDTO.getSku() + " ya está en uso");
            }
        }
        int deltaCantidad = productDTO.getCantidad() - product.getCantidad();

        product.setSku(productDTO.getSku());
        product.setNombre(productDTO.getNombre());
        product.setPrecio(productDTO.getPrecio());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);

        if (deltaCantidad !=0) {
            productRepository.actualizarStock(id, deltaCantidad);
        }

        return getProductById(id);
    }

    /**
     * Elimina un producto por su id.
     * @param id identificador del producto a eliminar
     * @throws ResourceNotFoundException si no existe un producto con ese id
     */
    @Transactional 
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con id " + id + " no existe"));

       productRepository.delete(product);
}

    // Convierte una Entity Product a su representación DTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setSku(product.getSku());
        dto.setNombre(product.getNombre());
        dto.setPrecio(product.getPrecio());
        dto.setCantidad(product.getCantidad());
        return dto;

    }

}
