package com.example.bank_service.accounting.dao;

import com.example.bank_service.accounting.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    // End of UserAccountRepository: Repository for user account; encapsulates MongoDB access for accounting.
}
