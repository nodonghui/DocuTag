package docuTag.domain.jwt.controller;


import docuTag.domain.jwt.service.BlackListService;
import docuTag.domain.jwt.refreshtoken.entity.RefreshToken;
import docuTag.domain.jwt.refreshtoken.service.RefreshTokenService;
import docuTag.domain.jwt.dto.UserRequsetDto;
import docuTag.domain.jwt.service.JwtService;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.service.UserService;
import docuTag.global.exception.ErrorResponse;
import docuTag.global.exception.ServiceException;
import docuTag.global.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final BlackListService blackListService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "RefreshToken 삭제 및 AccessToken 블랙리스트 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) throws AuthException {
        RefreshToken rt = refreshTokenService.findByToken(refreshToken);
        long userId = jwtUtil.getUserId(rt.getToken());
        refreshTokenService.deleteByUserId(userId);
        blackListService.addBlacklist(accessToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신", description = "RefreshToken으로 새로운 AccessToken을 쿠키로 발급합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공 (AccessToken 쿠키 재발급)"),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 RefreshToken",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request) {

            ResponseCookie accessTokenCookie = jwtService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok()
                    .header("Set-Cookie", accessTokenCookie.toString())
                    .body("액세스 토큰이 갱신되었습니다.");


    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "AccessToken으로 현재 로그인한 유저 정보를 반환합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequsetDto.class)
                    )),
            @ApiResponse(responseCode = "401", description = "유효하지 않거나 만료된 AccessToken",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<UserRequsetDto> getMe(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        long userId = jwtService.validateAndGetUserId(accessToken, refreshToken);
        User user = userService.findById(userId);

        return ResponseEntity.ok(new UserRequsetDto(user.getNickname()));
    }
}
