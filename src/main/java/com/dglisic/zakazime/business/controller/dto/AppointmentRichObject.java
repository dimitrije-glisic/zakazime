package com.dglisic.zakazime.business.controller.dto;

import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Review;
import jooq.tables.pojos.Service;

public record AppointmentRichObject(
    Appointment appointment,
    Service service,
    Employee employee,
    Customer customer,
    Business business,
    Review review
) {

}