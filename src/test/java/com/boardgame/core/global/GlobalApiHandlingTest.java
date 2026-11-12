package com.boardgame.core.global;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardgame.core.global.exception.BusinessException;
import com.boardgame.core.global.exception.CommonErrorCode;
import com.boardgame.core.global.exception.GlobalExceptionHandler;
import com.boardgame.core.global.response.GlobalResponseAdvice;

@WebMvcTest(GlobalTestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalResponseAdvice.class, GlobalExceptionHandler.class})
class GlobalApiHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void wrapsSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("COMMON-200"))
                .andExpect(jsonPath("$.result.value").value("ok"));
    }

    @Test
    void returnsFieldErrorsForInvalidRequest() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON-400"))
                .andExpect(jsonPath("$.result.name").value("이름은 필수입니다."));
    }

    @Test
    void returnsBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON-400"));
    }

    @Test
    void mapsBusinessExceptionToItsHttpStatus() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON-404"))
                .andExpect(jsonPath("$.message").value("요청한 리소스를 찾을 수 없습니다."));
    }

    @Test
    void returnsCommonResponseForUnknownPath() throws Exception {
        mockMvc.perform(get("/unknown-path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON-404"));
    }
}

@RestController
@RequestMapping("/test")
class GlobalTestController {

    @GetMapping("/success")
    GlobalTestResponse success() {
        return new GlobalTestResponse("ok");
    }

    @PostMapping("/validate")
    GlobalTestResponse validate(@Valid @RequestBody GlobalTestRequest request) {
        return new GlobalTestResponse(request.name());
    }

    @GetMapping("/not-found")
    void notFound() {
        throw new BusinessException(CommonErrorCode.NOT_FOUND);
    }
}

record GlobalTestRequest(@NotBlank(message = "이름은 필수입니다.") String name) {
}

record GlobalTestResponse(String value) {
}
