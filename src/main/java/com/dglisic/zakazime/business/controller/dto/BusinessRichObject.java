package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import jooq.tables.pojos.Business;
import jooq.tables.pojos.Employee;
import jooq.tables.pojos.Service;
import jooq.tables.pojos.UserDefinedCategory;

public record BusinessRichObject(
    Business business,
    List<Service> services,
    List<UserDefinedCategory> userDefinedCategories,
    List<Employee> employees
) {

}




