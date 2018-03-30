package com.mifan.article.domain;

import org.moonframework.model.mybatis.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * @author quzile
 * @version 1.0
 * @since 2017-07-04
 */
public class ExchangeRates extends BaseEntity {

    public static final String TABLE_NAME = "exchange_rates";

    public static final String FROM_CODE = "from_code";
    public static final String FROM_SIGN = "from_sign";
    public static final String TO_CODE = "to_code";
    public static final String TO_SIGN = "to_sign";
    public static final String RATE = "rate";

    private static final long serialVersionUID = 8031606581226905389L;

    private String fromCode;
    private String fromSign;
    private String toCode;
    private String toSign;
    private Double rate;

    private BigDecimal exchangeRate;

    public ExchangeRates() {
    }

    public ExchangeRates(Long id) {
        super(id);
    }

    /**
     * @return
     */
    public String getFromCode() {
        return fromCode;
    }

    /**
     * @param fromCode
     */
    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    /**
     * @return
     */
    public String getFromSign() {
        return fromSign;
    }

    /**
     * @param fromSign
     */
    public void setFromSign(String fromSign) {
        this.fromSign = fromSign;
    }

    /**
     * @return
     */
    public String getToCode() {
        return toCode;
    }

    /**
     * @param toCode
     */
    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    /**
     * @return
     */
    public String getToSign() {
        return toSign;
    }

    /**
     * @param toSign
     */
    public void setToSign(String toSign) {
        this.toSign = toSign;
    }

    /**
     * @return
     */
    public Double getRate() {
        return rate;
    }

    /**
     * @param rate
     */
    public void setRate(Double rate) {
        this.rate = rate;
        setExchangeRate(rate);
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setExchangeRate(Double rate) {
        if (rate != null) {
            this.exchangeRate = new BigDecimal(rate);
        }
    }
}
