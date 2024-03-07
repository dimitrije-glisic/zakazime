package com.dglisic.zakazime.business.domain;

import java.time.LocalDateTime;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;

public record ServiceEmployeeStartTime(Service service, Employee employee, LocalDateTime startTime) {
}
