package com.mifan.award.service.impl;

import com.mifan.award.dao.ProductDao;
import com.mifan.award.domain.Product;
import com.mifan.award.service.ProductService;
import org.moonframework.model.mongodb.service.AbstractBaseService;
import org.springframework.stereotype.Service;

@Service("productService")
public class ProductServiceImpl extends AbstractBaseService<Product, ProductDao> implements ProductService {
}
