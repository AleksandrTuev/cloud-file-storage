package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.mapper.PersonMapper;
import com.dev.cloud_file_storage.models.Person;
import com.dev.cloud_file_storage.repositories.PeopleRepository;
import com.dev.cloud_file_storage.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.SecurityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService { //этот Сервис специально для Spring Security
    private final PeopleRepository peopleRepository;
    private final PersonMapper personMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = peopleRepository.findByName(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", username))
        );
        return new PersonDetails(person);
    }

    public void register(PersonRegistrationDto personRegistrationDto) {
        //todo validate
        Person person = personMapper.toPerson(personRegistrationDto);
        person.setPassword(passwordEncoder.encode(personRegistrationDto.password()));
        peopleRepository.save(person);
    }

}
