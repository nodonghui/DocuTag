package docuTag.domain.user.controller;

import docuTag.domain.user.dto.LoginRequestDto;
import docuTag.domain.user.dto.SignupRequestDto;
import docuTag.domain.user.service.UserService;
import docuTag.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일/비밀번호로 회원가입합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "400Example",
                                    ref = "#/components/examples/400Example"
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "409Example",
                                    ref = "#/components/examples/409Example"
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
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 AccessToken을 쿠키로 발급합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (AccessToken 쿠키 발급)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "400Example",
                                    ref = "#/components/examples/400Example"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치",
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
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequestDto dto,
            HttpServletResponse response
    ) {
        userService.login(dto, response);
        return ResponseEntity.ok().build();
    }
}
