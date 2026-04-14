package com.vishal.ordering.inventory.repository;

import com.vishal.ordering.inventory.entity.InventoryItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItemEntity, Long> {

    Optional<InventoryItemEntity> findByProductCode(String productCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItemEntity i where i.productCode = :productCode")
    Optional<InventoryItemEntity> findForUpdateByProductCode(@Param("productCode") String productCode);
}
