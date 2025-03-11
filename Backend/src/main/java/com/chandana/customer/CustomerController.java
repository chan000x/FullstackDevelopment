package com.chandana.customer;

import org.springframework.http.HttpHeaders; 
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chandana.jwt.JWTUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



// API Layer
@RestController
@RequestMapping("api/v1/customers")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from your frontend
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }
   
   @GetMapping // Automatically path assigned because of requestmapping annotation in class.
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }


    @GetMapping("{customerId}") 
    public Customer getCustomer(
        @PathVariable("customerId") Integer customerId){

         return customerService.getCustomer(customerId);
        }
   
        @PostMapping
        public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
            customerService.addCustomer(request); // Add the customer
            String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER"); // Issue JWT token
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken) // Include "Bearer" prefix
                    .build();
        }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(
        @PathVariable("customerId") Integer customerId){
            customerService.deleteCustomerById(customerId);
        }

        @PutMapping("{customerId}")
        public void editCustomer(
            @RequestBody CustomerEditRequest request,
            @PathVariable("customerId") Integer customerId){
            customerService.editCustomer(customerId,request); // @Requestbody retrieves the json object from the post body client sent.
        }
    
}
