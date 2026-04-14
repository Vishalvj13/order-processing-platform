package com.vishal.ordering.inventory.controller;

import com.vishal.ordering.inventory.dto.InventoryResponse;
import com.vishal.ordering.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productCode}")
    public InventoryResponse getInventory(@PathVariable String productCode) {
        return inventoryService.getInventory(productCode);
    }
}
