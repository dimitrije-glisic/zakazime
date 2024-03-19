package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import jooq.tables.pojos.Customer;

public record CustomerDto (
    Customer customer,
    List<AppointmentRichObject> appointments
) {
}

