package com.dglisic.zakazime.business.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class WorkingHoursRequest {
  private List<WorkingHoursItem> workingHours;
}
