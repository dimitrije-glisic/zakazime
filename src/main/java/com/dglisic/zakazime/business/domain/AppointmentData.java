package com.dglisic.zakazime.business.domain;

import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;
import lombok.Data;

@Data
public class AppointmentData {
  private Customer customer;
  private Business business;
  private Service service;
  private Employee employee;
  private Appointment appointment;
}
