package com.project.bookstudy.security.repository;

import com.project.bookstudy.security.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    Optional<RefreshToken> findByMemberId(Long id);


}
