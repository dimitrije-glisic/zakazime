package com.dglisic.zakazime.service;

import model.tables.records.BusinessProfileRecord;

public interface BusinessService {

  BusinessProfileRecord getBusinessProfileForUser(String userEmail);

}
