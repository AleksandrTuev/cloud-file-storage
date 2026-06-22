package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.models.Person;
import com.dev.cloud_file_storage.repositories.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PasswordEncoder passwordEncoder;
    private final PeopleRepository peopleRepository;

    public Person create(Person person) {
//        try {
            person.setPassword(passwordEncoder.encode(person.getPassword()));
            return peopleRepository.save(person);
//        } catch (Exception e) {
//
//        //todo обработать валидацию
//        //todo обработать случай, когда в БД имеется такой login
//
//        }
    }
}
