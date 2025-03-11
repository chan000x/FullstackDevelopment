package com.chandana.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import com.chandana.AbstractTestcontainers;
import com.github.javafaker.Faker;

//@DataJdbcTest // Auto load the all the beans that neede to run our test case of JDBC Template.
// But we do not need this. We can load neccessary beans by our selves.
public class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers{

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    
    @BeforeEach
    void setUp() {
        // We intialise this object here because for each test case it needs a fresh object.
        // This method execute before each method.
        // CustomerRow Mapper does not need fresh instance each time run a test case.
        underTest = new CustomerJDBCDataAccessService(
            getJdbcTemplate(),
            customerRowMapper
        );
    }


    @Test
    void testSelectAllCustomers() {


        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );



        underTest.insertCustomer(customer);

        List<Customer> customers =  underTest.selectAllCustomers();
        
        assertThat(customers).isNotEmpty();
    }

    @Test
    void testSelectCustomerById() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );

        underTest.insertCustomer(customer);
       // Long id = customer.getId();
        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });
    }

    @Test
    void willReturnWhenSelectCustomerById() {
        int id = -1;
       
        var actual = underTest.selectCustomerById(id);

        assertThat(actual).isEmpty();
    }


    @Test
    void testDeleteCustomerById() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(c -> c.getId())
        .findFirst()
        .orElseThrow();

       // Optional<Customer> exist = underTest.selectCustomerById(id);

        // When
        underTest.deleteCustomerById(id);

        // Then
        Optional<Customer> afterDelete = underTest.selectCustomerById(id);
               // Assert the result
        assertThat(afterDelete).isNotPresent();
    }

    @Test
    void testExistsPersonWithEmail() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);
        // Integer id = underTest.selectAllCustomers()
        //         .stream()
        //         .filter(c -> c.getEmail().equals(email))
        //         .map(c -> c.getId())
        //         .findFirst()
        //         .orElseThrow();
        boolean actual = underTest.existsPersonWithEmail(email);

        assertThat(actual).isTrue();

    }

    @Test
    void testExistsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
 
        // When
        boolean actual = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isFalse();

    }

    @Test
    void testExistsPersonWithId() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
        .stream()
        .filter(c -> c.getEmail().equals(email))
        .map(c -> c.getId())
        .findFirst()
        .orElseThrow();

        // Fetch the ID of the saved customer
        var actual = underTest.existsPersonWithId(id);
    

        assertThat(actual).isTrue();
    }
    @Test
    void testExistsPersonWithIdReturnFalseWhenIdNotPresent() {

        // Given
        int id = -1;

        // When
        var actual = underTest.existsPersonWithId(id);
    
        // Then
        assertThat(actual).isFalse();
    }
    

    @Test
    void testInsertCustomer() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
            // Randomly assign gender
            Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
    
        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

         // Validate if the customer exists by ID
        boolean personExists = underTest.existsPersonWithId(id);
    
        // Assert the result
        assertThat(personExists).isTrue();
    }



    @Test
    void testUpdateCustomerName() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var newName = "foo";
        // When age is name
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });

    }
    @Test
    void testUpdateCustomerEmail() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var newEmail = "foo@gmail.com";

        // When email is changed
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });
    }
    @Test
    void testUpdateCustomerGender() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var newGender = Gender.FEMALE;

        // When email is changed
        Customer update = new Customer();
        update.setId(id);
        update.setGender(newGender);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(newGender);

        });

    }
    @Test
    void testUpdateCustomerAge() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var age = 45;
        // When age is name
        Customer update = new Customer();
        update.setId(id);
        update.setAge(age);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(age);
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });

    }

    @Test
    void testUpdateCustomerAllProperties() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        Customer customer = new Customer(
            fullName,
            email,
            random.nextInt(18,100),
            gender
        );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        var newAge = 45;
        var newName = "foo";
        var newEmail = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var newGender = Gender.FEMALE;
        // When age is name
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);
        update.setEmail(newEmail);
        update.setName(newName);
        update.setGender(newGender);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getGender()).isEqualTo(newGender);

        });

    }

    @Test
    void testNoUpdateCustomerWhenNotNothinUpdate() {
        Faker faker = new Faker();
        var name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();
        String title = name.title();
        Random random = new Random();
        String fullName = firstName + " " +lastName;
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

    
        // Randomly assign gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

         Customer customer = new Customer(
             fullName,
             email,
             random.nextInt(18,100),
             gender
         );
    
        // Save the customer to the database
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        // When age is name
        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });

    }
    
}
