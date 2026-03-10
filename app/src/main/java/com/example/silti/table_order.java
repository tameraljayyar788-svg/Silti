package com.example.silti;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "orders",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Address.class,
                        parentColumns = "id",
                        childColumns = "addressId",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("userId"), @Index("addressId")}
)public class table_order {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "addressId")
    private long addressId;

    @ColumnInfo(name = "orderNumber")
    private String orderNumber;

    @ColumnInfo(name = "subtotal")
    private double subtotal;

    @ColumnInfo(name = "discount")
    private double discount;

    @ColumnInfo(name = "shipping")
    private double shipping;

    @ColumnInfo(name = "total")
    private double total;

    @ColumnInfo(name = "paymentMethod")
    private String paymentMethod;

    @ColumnInfo(name = "paymentStatus")
    private String paymentStatus;

    @ColumnInfo(name = "orderStatus")
    private String orderStatus;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "createdAt")
    private long createdAt;

    @ColumnInfo(name = "updatedAt")
    private long updatedAt;

    // Constructors
    public table_order() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.orderStatus = "pending";
        this.paymentStatus = "pending";
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getAddressId() { return addressId; }
    public void setAddressId(long addressId) { this.addressId = addressId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getShipping() { return shipping; }
    public void setShipping(double shipping) { this.shipping = shipping; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
