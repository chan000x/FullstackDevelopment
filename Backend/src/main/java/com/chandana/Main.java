package com.chandana;

import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.chandana.customer.Customer;
import com.chandana.customer.CustomerRepository;
import com.chandana.customer.Gender;
import com.github.javafaker.Faker;



@SpringBootApplication(scanBasePackages = "com.chandana.customer")
@ComponentScan(basePackages = {"com.chandana.security","com.chandana.jwt", "com.chandana.customer"})
public class Main {  
   
    public static void main(String[] args){
            SpringApplication.run(Main.class, args);
    }


    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args ->{
            Faker faker = new Faker();
            Random random = new Random();
    
            // Randomly assign gender
            Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
    
            // Generate names based on gender
            String firstName = gender.equals("Male") ? faker.name().firstName() : faker.name().firstName();
            String lastName = faker.name().lastName();
    
            // Generate customer
            Customer customer = new Customer(
                firstName + " " + lastName,
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com",
                random.nextInt(18, 100), // Random age between 18 and 100
                gender
            );
    
            // Print customer details
            System.out.println("\n#################################");
            System.out.println("Customer Details: ");
            System.out.println(customer);
        customerRepository.save(customer);
        };
    }
}  
