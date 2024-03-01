package com.dglisic.zakazime.business.controller.dto;

import lombok.Getter;

@Getter
public enum ServiceKind {
  HAIR_SERVICES("Frizerske usluge"),
  COSMETIC_SERVICES("Kozmetičke usluge"),
  MASSAGE("Masaže"),
  OTHER("Ostalo");

  private final String value;

  ServiceKind(String value) {
    this.value = value;
  }

}
