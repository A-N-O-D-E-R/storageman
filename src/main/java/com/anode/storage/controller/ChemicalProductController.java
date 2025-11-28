package com.anode.storage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.anode.storage.entity.ChemicalProduct;
import com.anode.storage.service.ChemicalProductService;
import com.anode.storage.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ChemicalProductController {

    private final ChemicalProductService service;
    private final InventoryService inventoryService;
    @Autowired
    private SimpMessagingTemplate messaging;


    @GetMapping
    public List<ChemicalProduct> findAll() {
        return service.findAll();
    }

    @PostMapping
    public ChemicalProduct create(@RequestBody ChemicalProduct p) {
        return service.create(p);
    }

    @PutMapping("/{id}")
    public ChemicalProduct update(@PathVariable Long id, @RequestBody ChemicalProduct p) {
        return service.update(id, p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(
            @RequestParam Long productId,
            @RequestParam Long locationId) {

        return ResponseEntity.ok(service.checkProductLocation(productId, locationId));
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> qr(@PathVariable Long id) throws Exception {
        ChemicalProduct p = service.find(id);
        byte[] qr = inventoryService.generateQrCode("CHEM-" + p.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(qr);
    }


    public void sendLowStockAlert(ChemicalProduct p) {
        messaging.convertAndSend("/topic/low-stock", p);
    }

}
