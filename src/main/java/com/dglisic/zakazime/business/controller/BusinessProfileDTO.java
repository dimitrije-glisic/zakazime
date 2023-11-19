package com.dglisic.zakazime.business.controller;

import lombok.Builder;

@Builder
public record BusinessProfileDTO(String name, String type, String phoneNumber, String city, String postalCode, String address, String status, String ownerName)
{}
