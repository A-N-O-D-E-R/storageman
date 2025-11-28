package com.anode.storage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.anode.storage.entity.core.ProductType;
import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.service.InventoryService;
import com.anode.storage.service.StorageItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class StorageItemController {

    private final StorageItemService service;
    private final InventoryService inventoryService;
    @Autowired
    private SimpMessagingTemplate messaging;


    @GetMapping
    public List<StorageItem> findAll(@RequestParam(required = false) ProductType productType) {
        if (productType != null) {
            return service.findByProductType(productType);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public StorageItem findById(@PathVariable Long id) {
        return service.find(id);
    }

    @PostMapping
    public StorageItem create(@RequestBody StorageItem item) {
        return service.create(item);
    }

    @PutMapping("/{id}")
    public StorageItem update(@PathVariable Long id, @RequestBody StorageItem item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(
            @RequestParam Long itemId,
            @RequestParam Long locationId) {

        return ResponseEntity.ok(service.checkItemLocation(itemId, locationId));
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> qr(@PathVariable Long id) throws Exception {
        StorageItem item = service.find(id);
        String qrData = item.getProductType().getPrefix() + "-" + item.getId();
        byte[] qr = inventoryService.generateQrCode(qrData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(qr);
    }


    public void sendLowStockAlert(StorageItem item) {
        messaging.convertAndSend("/topic/low-stock", item);
    }

}
