package com.example.Buoi6_Products.controller;


import com.example.Buoi6_Products.entity.Product;
import com.example.Buoi6_Products.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RequestMapping("/")
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("")
    public String GetListBooks(Model model){
        model.addAttribute("list",productService.getAll());
        model.addAttribute("title","Product List");
        return "product/list";
    }
    @GetMapping("/search")
    public String searchProduct(@RequestParam("input") String input, Model model) {
        List<Product> searchResults = productService.search(input);
        model.addAttribute("list", searchResults);
        model.addAttribute("title", "Product " + input);
        return "product/list";
    }

    @GetMapping("/create")
    public String addProduct(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("title","Add Product");
        return "product/add";
    }
    @PostMapping("/create")
    public String addProduct(@ModelAttribute("product") @Valid Product product , BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        // Kiểm tra ràng buộc và đặt thông báo lỗi vào BindingResult
//        @Null @RequestParam MultipartFile imageProduct
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "product/add";
        } else {
            // Nếu không có lỗi, thêm sách và chuyển hướng đến trang danh sách sách
            product.setId(Init());
            if(imageProduct != null && imageProduct.getSize()>0)
            {
                try{
                    File saveFile = new ClassPathResource("static/images").getFile();
                    String newImageFile = UUID.randomUUID() + ".png";
                    java.nio.file.Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                    Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    product.setImage(newImageFile);
                }catch (Exception ex){
                    ex.printStackTrace();
                    return "product/add";
                }
            }


            productService.addProduct(product);

            return "redirect:/";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") long id,Model model){
        Product editProduct=productService.getProductById(id);
        if(editProduct!=null){
            model.addAttribute("product",editProduct);
            model.addAttribute("title","Edit product");
            return "product/edit";
        }
        return "not-found";

    }
    @PostMapping("/edit")
    public String editProduct(@ModelAttribute("product") @Valid Product product , BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        // Kiểm tra ràng buộc và đặt thông báo lỗi vào BindingResult
//        @Null @RequestParam MultipartFile imageProduct
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            return "product/edit";
        } else {
            // Nếu không có lỗi, thêm sách và chuyển hướng đến trang danh sách sách
            if(imageProduct != null && imageProduct.getSize()>0)
            {
                try{
                    File saveFile = new ClassPathResource("static/images").getFile();
                    String newImageFile = UUID.randomUUID() + ".png";
                    java.nio.file.Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                    Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    product.setImage(newImageFile);
                }catch (Exception ex){
                    ex.printStackTrace();
                    return "product/edit";
                }
            }


            productService.update(product.getId(),product);

            return "redirect:/";
        }
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id){
        Product product=productService.getProductById(id);
        if(product!=null){
            productService.delete(id);
            return "redirect:/";
        }
        return "not-found";
    }

    private long Init(){
        long maxID=0;
        for (Product b : productService.getAll()) if(maxID<b.getId()) maxID=b.getId();
        return maxID+1;
    }
}