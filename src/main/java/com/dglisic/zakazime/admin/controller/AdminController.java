package com.dglisic.zakazime.admin.controller;

import com.dglisic.zakazime.admin.controller2service_mapper.BusinessTypeMapper;
import com.dglisic.zakazime.admin.service.BusinessTypeService;
import com.dglisic.zakazime.business.domain.BusinessType;
import com.dglisic.zakazime.common.MessageDTO;
import com.dglisic.zakazime.user.domain.User;
import com.dglisic.zakazime.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;
  private final BusinessTypeService businessTypeService;
  private final BusinessTypeMapper businessTypeMapper;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/business-types")
  public ResponseEntity<List<BusinessTypeDTO>> getAllBusinessTypes() {
    final List<BusinessType> businessTypes = businessTypeService.getAllBusinessTypes();
    final List<BusinessTypeDTO> businessTypeDTOs = businessTypeMapper.toDTO(businessTypes);
    return ResponseEntity.ok(businessTypeDTOs);
  }

  @GetMapping("/admin/business-types/{id}")
  public ResponseEntity<BusinessTypeDTO> getBusinessTypeById(@PathVariable final int id) {
    final BusinessType businessType = businessTypeService.getBusinessTypeById(id);
    final BusinessTypeDTO dto = businessTypeMapper.toDTO(businessType);
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/admin/business-types")
  public ResponseEntity<BusinessTypeDTO> createBusinessType(@RequestBody @Valid final CreateBusinessTypeRequest request) {
    final BusinessType toBeCreated = businessTypeMapper.toDomain(request);
    final BusinessType created = businessTypeService.createBusinessType(toBeCreated);
    final BusinessTypeDTO dto = businessTypeMapper.toDTO(created);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PutMapping("/admin/business-types/{id}")
  public ResponseEntity<MessageDTO> updateBusinessType(@RequestBody final BusinessType request) {
    businessTypeService.updateBusinessType(request);
    return ResponseEntity.ok(new MessageDTO("Business type updated successfully"));
  }

  @DeleteMapping("/admin/business-types/{id}")
  public ResponseEntity<MessageDTO> deleteBusinessType(@PathVariable final int id) {
    businessTypeService.deleteBusinessType(id);
    return ResponseEntity.ok(new MessageDTO("Business type deleted successfully"));
  }

}
