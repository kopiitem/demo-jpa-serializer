package com.kopiitem.demojpa;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class DemoJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoJpaApplication.class, args);
    }

}

@Configuration
@EnableWebMvc
class WebConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

@RestController
@RequestMapping("/api")
class UserResources {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(value = "/users",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<User> getAllusers() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/restTemplate",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public List<User> restTemplate() {
        String url = "http://localhost:8080/api/users";
        return restTemplate.getForEntity(url, ArrayList.class).getBody();
        //return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, ArrayList.class, (Object) null).getBody();
    }

}

@Component
class CDLR implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        Stream.of(new User("Donny"), new User("Wice"), new User("Kensei")).forEach(userRepository::save);
    }
}

interface UserRepository extends JpaRepository<User, Long> {

}

class UserSerialize extends JsonSerializer<User> {

    @Override
    public void serialize(User t, JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        jg.writeNumberField("id", t.getId());
        jg.writeStringField("userName", t.getName());
        jg.writeEndObject();
    }

}


@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize(using = UserSerialize.class)
class User {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    public User(String name) {
        this.name = name;
    }

}
