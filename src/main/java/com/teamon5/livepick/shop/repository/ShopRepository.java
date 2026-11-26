package com.teamon5.livepick.shop.repository;

import com.teamon5.livepick.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}