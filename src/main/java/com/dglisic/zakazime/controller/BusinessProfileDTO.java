package com.dglisic.zakazime.controller;

import lombok.Builder;

@Builder
public record BusinessProfileDTO(String name, String phoneNumber, String city, String postalCode, String address, String status, String ownerName)
{}
