package com.mifan.article.domain;

import com.mifan.article.domain.support.Brands;
import com.mifan.article.domain.support.Currency;
import org.moonframework.model.mybatis.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class TopicsProduct extends BaseEntity {

    public static final String TABLE_NAME = "topics_product";

    public static final String TOPIC_ID = "topic_id";
    public static final String PRICE = "price";
    public static final String PRICE_UNIT = "price_unit";
    public static final String BRAND = "brand";
    public static final String SALE_RANK = "sale_rank";

    private static final long serialVersionUID = -6602665015318504256L;

    private Long topicId;
    private BigDecimal price;
    private String priceUnit;
    private String brand;
    private Long saleRank;

    private List<TopicsProductHistory> histories;
    private Brands brandInfo;
    private String priceSign;

    public TopicsProduct() {
    }

    public TopicsProduct(Long id) {
        super(id);
    }

    public void brand(String name, Function<String, Brands> function) {
        if (name == null) {
            return;
        }

        Brands brand = function.apply(name);
        if (brand == null) {
            brand = new Brands();
            brand.setName(name);
        }
        setBrandInfo(brand);
    }

    @Override
    public String toString() {
        return brand;
    }

    public void exchange(List<ExchangeRates> rates) {
        if (this.priceUnit == null) {
            this.price = null;
            this.histories = null;
        } else {
            Currency currency = Currency.from(priceUnit);
            this.priceSign = currency == null ? null : currency.getSign();
        }
        rates.stream().filter(rate -> rate.getFromCode().equals(this.priceUnit) && this.price != null).forEach(rate -> {
            this.priceSign = rate.getToSign();
            this.priceUnit = rate.getToCode();
            this.price = this.price.multiply(rate.getExchangeRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (this.histories != null) {
                for (TopicsProductHistory history : this.histories) {
                    history.setPrice(history.getPrice().multiply(rate.getExchangeRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        });
    }

    /**
     * @return 主题ID
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * @param topicId 主题ID
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * @return 价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price 价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return 价格单位
     */
    public String getPriceUnit() {
        return priceUnit;
    }

    /**
     * @param priceUnit 价格单位
     */
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    /**
     * @return 品牌
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand 品牌
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return
     */
    public Long getSaleRank() {
        return saleRank;
    }

    /**
     * @param saleRank
     */
    public void setSaleRank(Long saleRank) {
        this.saleRank = saleRank;
    }

    public List<TopicsProductHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<TopicsProductHistory> histories) {
        this.histories = histories;
    }

    public Brands getBrandInfo() {
        return brandInfo;
    }

    public void setBrandInfo(Brands brandInfo) {
        this.brandInfo = brandInfo;
    }

    public String getPriceSign() {
        return priceSign;
    }

    public void setPriceSign(String priceSign) {
        this.priceSign = priceSign;
    }
}
