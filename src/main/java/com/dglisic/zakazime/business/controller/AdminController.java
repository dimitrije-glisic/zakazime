package com.dglisic.zakazime.business.controller;


import com.dglisic.zakazime.business.controller.dto.RejectBusinessRequest;
import com.dglisic.zakazime.business.service.AdminService;
import com.dglisic.zakazime.common.MessageResponse;
import java.util.List;
import jooq.tables.pojos.Business;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/businesses/waiting-for-approval")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Business>> getAllWaitingForApproval() {
    return ResponseEntity.ok(adminService.getAllWaitingForApproval());
  }

  @PostMapping("/businesses/{businessId}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> approveBusiness(@PathVariable Integer businessId) {
    adminService.approveBusiness(businessId);
    return ResponseEntity.ok(new MessageResponse("Business approved successfully"));
  }

  @PostMapping("/businesses/{businessId}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> rejectBusiness(@PathVariable Integer businessId,
                                                        @RequestBody RejectBusinessRequest request) {
    adminService.rejectBusiness(businessId, request.reason());
    return ResponseEntity.ok(new MessageResponse("Business rejected successfully"));
  }

}
