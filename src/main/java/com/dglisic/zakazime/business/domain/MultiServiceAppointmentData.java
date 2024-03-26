package com.dglisic.zakazime.business.domain;

import java.util.List;
import java.util.Map;
import jooq.tables.pojos.Appointment;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Customer;
import lombok.Data;

@Data
public class MultiServiceAppointmentData {
  private Customer customer;
  private Business business;
  private Map<Integer, ServiceEmployeeStartTime> serviceEmployeeStartTimeMap;
  private List<Appointment> appointments;
}
