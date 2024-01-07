package com.dglisic.zakazime.business.service;

import jakarta.annotation.Nullable;
import java.util.List;
import jooq.tables.pojos.Service;

public interface ServiceService {

  List<Service> searchServiceTemplates(@Nullable final String businessType, @Nullable final String category,
                                       @Nullable final String subcategory);

}
