package com.example.Buoi6_Products.services;


import com.example.Buoi6_Products.entity.Product;
import com.example.Buoi6_Products.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private IProductRepository productsRepository;

    public List<Product> getAll()
    {
        return productsRepository.findAll();
    }

    public Product getProductById(Long id){
        Optional<Product> optional = productsRepository.findById(id);
        return optional.orElse(null);
    }
    public void addProduct(Product products){
        productsRepository.save(products);
    }
    public void update(long id, Product editProduct){
        Product findProduct=getProductById(id);
        if(findProduct!=null){
            productsRepository.delete(findProduct);
            productsRepository.save(editProduct);
        }
    }
    public void delete(long  id){
        Product findProduct=getProductById(id);
        if(findProduct!=null){
            productsRepository.delete(findProduct);
        }
    }
    public List<Product> search(String name) {
        if (name.isBlank()) {
            return productsRepository.findAll(); // Trả về tất cả sản phẩm nếu không có tên được cung cấp
        }
        return productsRepository.findByNameContaining(name);
    }

}
