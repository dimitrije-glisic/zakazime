package com.dglisic.zakazime.business.service;

import java.util.List;
import jooq.tables.pojos.Business;

public interface AdminService {

  List<Business> getAllWaitingForApproval();

  void approveBusiness(Integer businessId);

  void rejectBusiness(Integer businessId, String reason);
}
