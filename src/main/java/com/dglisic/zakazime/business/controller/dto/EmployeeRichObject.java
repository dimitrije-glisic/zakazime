package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;

public record EmployeeRichObject(
    Employee employee,
    List<Service> services,
    List<WorkingHoursItem> workingHours
) {
}
