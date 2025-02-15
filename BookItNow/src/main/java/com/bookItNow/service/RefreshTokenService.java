package com.bookItNow.service;

import com.bookItNow.exception.UserNotFoundException;
import com.bookItNow.model.RefreshToken;
import com.bookItNow.model.User;
import com.bookItNow.repository.RefreshTokenRepository;
import com.bookItNow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String userName){
        User user = userRepository.findByUsername(userName).orElseThrow(()->new UserNotFoundException(userName));

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        RefreshToken refreshToken;
        if(existingToken.isPresent()){
            refreshToken = existingToken.get().builder()
                    .token(UUID.randomUUID().toString())
                    .expires(Instant.now().plusMillis(60000))
                    .build();
        }
        else {
            refreshToken = new RefreshToken().builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expires(Instant.now().plusMillis(60000))
                    .build();
        }
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByRefreshToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpires().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " expired");
        }
        return token;
    }

    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
