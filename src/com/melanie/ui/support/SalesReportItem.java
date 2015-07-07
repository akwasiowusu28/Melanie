package com.melanie.ui.support;


import java.io.Serializable;
import java.util.Date;

public class SalesReportItem implements Serializable, Comparable<SalesReportItem> {

    private String description;
    private int quantity;
    private double total;
    private Date saleDate;

    public SalesReportItem(String description, int quantity, double total, Date saleDate) {
        this.description = description;
        this.quantity = quantity;
        this.total = total;
        this.saleDate = saleDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesReportItem)) return false;

        SalesReportItem that = (SalesReportItem) o;

        return getQuantity() == that.getQuantity()
                && Double.compare(that.getTotal(), getTotal()) == 0
                && getDescription().equals(that.getDescription())
                && getSaleDate().equals(that.getSaleDate());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getDescription().hashCode();
        result = 31 * result + getQuantity();
        temp = Double.doubleToLongBits(getTotal());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getSaleDate().hashCode();
        return result;
    }

    @Override
    public int compareTo(SalesReportItem another) {
        return another != null && another.getDescription() != null ?
                another.getDescription().compareTo(getDescription()) : 0;
    }
}
