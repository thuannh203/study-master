package com.example.study.web;

import com.example.study.common.CommonTimestamp;
import com.example.study.exception.HttpBadRequestException;
import com.example.study.exception.HttpConflictException;
import com.example.study.exception.HttpNotFoundException;
import com.example.study.exception.NotFoundException;
import com.example.study.model.Customer;
import com.example.study.model.CustomerService;
import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.PUT, RequestMethod.POST} )
public class CustomerController {
    @Autowired
    CustomerService customerService;


    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasAnyAuthority('USER_CREATE')")
    public ResponseEntity<?> registerCustomer(@Validated @RequestBody RegisterCustomerRequest request
    ,@RequestParam(name = "rollbackFlag", required = false) boolean rollbackFlag) {
        try {
            Customer customer = request.get();
            customer.setRegisterTimestamp(CommonTimestamp.currentTimestamp());
            Customer cart = customerService.create(customer, rollbackFlag);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | ValidationException e) {
            throw new HttpBadRequestException(e.getMessage());
        } catch (HibernateException e) {
            throw new HttpConflictException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{customerCode}", method = RequestMethod.GET)
    @PreAuthorize("hasAnyAuthority('USER_READ')")
    public ResponseEntity<?> findCustomer(@PathVariable String customerCode) {
        Customer customer = Optional.ofNullable(customerService.findCustomer(customerCode))
                .orElseThrow(() -> new HttpNotFoundException("Cannot find customer" + customerCode));
        return ResponseEntity.ok(customer);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasAnyAuthority('USER_READ')")
    public ResponseEntity<?> findCustomer(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(name = "size", required = false, defaultValue = "100") int size) {

        try {
            Map<String, Object> result = customerService
                    .findCustomer(PageRequest.of(page, size, Sort.by("registerTimestamp")));
            result.put("current_page", page);
        return ResponseEntity.ok(result);
    }catch (NotFoundException e){
            throw new HttpNotFoundException("Cannot find customer");
        }
    }

    @RequestMapping(value = "/{customerCode}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('USER_DELETE')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String customerCode,
                                            @RequestParam(name = "rollbackFlag", required = false) boolean rollbackFlag) {
        try {
            Customer customer = customerService.deleteCustomer(customerCode, rollbackFlag);
            return ResponseEntity.ok(customer);
        } catch (NotFoundException e) {
            throw new HttpNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException(e.getMessage());
        } catch (HibernateException e) {
            throw new HttpConflictException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{customerCode}", method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('USER_UPDATE')")
    public ResponseEntity<?> upsertCustomer(@Validated @RequestBody RegisterCustomerRequest request,
                                            @PathVariable(name = "customerCode") String customerCode,
                                            @RequestParam(name = "rollbackFlag", required = false) boolean rollbackFlag) {
        try {
            Customer customer = request.get();
            Customer cart = customerService.upsert(customerCode, customer, rollbackFlag);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | ValidationException e) {
            throw new HttpBadRequestException(e.getMessage());
        } catch (HibernateException e) {
            throw new HttpConflictException(e.getMessage());
        }
    }
}
