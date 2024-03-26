package com.dglisic.zakazime.business.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public enum AppointmentStatus {
  SCHEDULED,
  CONFIRMED,
  COMPLETED,
  RESCHEDULED,
  CANCELLED,
  NO_SHOW;


  //in this case admin is business owner or business admin - service provider
  private static final Map<AppointmentStatus, Set<AppointmentStatus>> ADMIN_TRANSITIONS = new HashMap<>();
  private static final Map<AppointmentStatus, Set<AppointmentStatus>> USER_TRANSITIONS = new HashMap<>();

  static {
    ADMIN_TRANSITIONS.put(AppointmentStatus.SCHEDULED, EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED));
    ADMIN_TRANSITIONS.put(AppointmentStatus.CONFIRMED,
        EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW, AppointmentStatus.CANCELLED));
    // Add more transitions for ADMIN as needed

    USER_TRANSITIONS.put(AppointmentStatus.SCHEDULED, EnumSet.of(AppointmentStatus.CANCELLED));
    USER_TRANSITIONS.put(AppointmentStatus.CONFIRMED, EnumSet.of(AppointmentStatus.CANCELLED));
    // Add more transitions for USER as needed
  }

  public static boolean canTransition(AppointmentStatus from, AppointmentStatus to, boolean isAdmin) {
    if (isAdmin) {
      return ADMIN_TRANSITIONS.get(from).contains(to);
    } else {
      return USER_TRANSITIONS.get(from).contains(to);
    }
  }

}
