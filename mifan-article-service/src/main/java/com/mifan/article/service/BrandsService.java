package com.mifan.article.service;

import java.util.List;

import com.mifan.article.domain.support.Brands;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/8/2
 */
public interface BrandsService {

    /**
     * <p>ignore cases</p>
     *
     * @param name brand name
     * @return brand
     */
    Brands findBrand(String name);
    
    /**
     * 产品中心48个热门品牌
     * @return
     */
    List<Brands> hotBrands();

    /**
     * <p>ignore cases</p>
     *
     * @param name   brand name
     * @param cached cached
     * @return brand
     */
    Brands findBrand(String name, boolean cached);

    /**
     * <p>reset cache by name</p>
     *
     * @param name name
     * @return if successful
     */
    boolean reset(String name);

}
