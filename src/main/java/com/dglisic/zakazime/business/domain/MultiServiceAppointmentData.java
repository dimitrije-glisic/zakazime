package com.dglisic.zakazime.business.domain;

import java.util.List;
import java.util.Map;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

@Data
public class MultiServiceAppointmentData {
  private Customer customer;
  private Business business;
  private Map<Integer, Pair<Service, Employee>> serviceEmployeeMap;
  private List<Appointment> appointments;
}
