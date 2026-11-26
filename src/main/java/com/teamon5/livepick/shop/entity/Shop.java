package com.teamon5.livepick.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop")
@Getter
@NoArgsConstructor
public class Shop {

    @Id
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "shop_uuid", nullable = false, length = 36)
    private String shopUuid;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "shop_description", columnDefinition = "TEXT")
    private String shopDescription;

    @Column(name = "shop_url", length = 255)
    private String shopUrl;

    @Column(name = "shop_status", nullable = false, length = 30)
    private String shopStatus;

    @Column(name = "customer_service_phone", length = 30)
    private String customerServicePhone;

    @Column(name = "customer_service_email", length = 255)
    private String customerServiceEmail;

    @Column(name = "shop_address", length = 500)
    private String shopAddress;

    @Column(name = "logo_media_id")
    private Long logoMediaId;

    @Column(name = "banner_media_id")
    private Long bannerMediaId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}