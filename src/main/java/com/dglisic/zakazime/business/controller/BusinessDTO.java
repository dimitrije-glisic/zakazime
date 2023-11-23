package com.dglisic.zakazime.business.controller;

import java.util.List;
import lombok.Builder;

@Builder
public record BusinessDTO(String name, String type, String phone, String city, String postalCode, String address,
                          String status, String ownerName, List<ServiceDTO> services) {
}
