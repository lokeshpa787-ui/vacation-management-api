package vacation.application.controller.auth;

import vacation.application.dto.TokenRequestDto;
import vacation.application.dto.TokenResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtEncoder jwtEncoder;

    @PostMapping("/token")
    public TokenResponseDto generateToken(
            @Valid @RequestBody TokenRequestDto request) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("vacation-app")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(request.getEmployeeId().toString())
                .claim("roles", List.of(request.getRole()))
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(() -> "HS256").build(),
                        claims
                )
        ).getTokenValue();

        return new TokenResponseDto(token);
    }
}
