package com.me.kaffe.configuration.seeder;

import com.me.kaffe.entity.Account;
import com.me.kaffe.entity.Role;
import com.me.kaffe.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AccountSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountSeeder(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed users
        if (accountRepository.findByUsername("user").isEmpty()) {
            Account user = new Account();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(Role.CUSTOMER);
            accountRepository.save(user);
        }

        if (accountRepository.findByUsername("admin").isEmpty()) {
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            accountRepository.save(admin);
        }
    }
}
