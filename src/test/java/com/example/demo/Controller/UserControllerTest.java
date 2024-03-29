package com.example.demo.Controller;

import com.example.demo.Config.Doc.RestDocsSetUp;
import com.example.demo.DTO.UserDto;
import com.example.demo.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends RestDocsSetUp {

    private final ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Autowired
    public UserControllerTest(RestDocumentationResultHandler restDocs, MockMvc mockMvc,
            ObjectMapper objectMapper) {
        super(restDocs, mockMvc);
        this.objectMapper = objectMapper;
    }

    @Test
    public void getUser() throws Exception {
        UserDto dto = UserDto.builder().uid(1L).email("email1@gmail.com").username("user1").profilePic("imgLink")
                .build();
        when(userService.findByUid(any(Long.class))).thenReturn(dto);

        mvc.perform(get("/user/{uid}", 1L).with(oauth2Login()))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("uid").description("유저 PK")
                        ),
                        getResponseUserSnippet()
                ))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser() throws Exception {
        UserDto dto = UserDto.builder().uid(1L).email("email1@gmail.com").username("user1").profilePic("imgLink")
                .build();
        when(userService.updateUser(any(UserDto.class))).thenReturn(dto);

        mvc.perform(patch("/user").with(oauth2Login()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(restDocs.document(
                        getRequestUserSnippet()
                ))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUser() throws Exception {
        when(userService.deleteUser()).thenReturn(1L);

        mvc.perform(delete("/user").with(oauth2Login()).header("Authorization","bearer-"))
                .andDo(restDocs.document())
                .andExpect(status().isNoContent());
    }

    private Snippet getRequestUserSnippet() {
        return requestFields(
                fieldWithPath("uid").description("유저 PK"),
                fieldWithPath("email").description("유저 이메일"),
                fieldWithPath("username").description("유저명"),
                fieldWithPath("profilePic").description("프로필 사진"));
    }

    private Snippet getResponseUserSnippet() {
        return responseFields(
                fieldWithPath("uid").description("유저 PK"),
                fieldWithPath("username").description("유저명"),
                fieldWithPath("email").description("유저 이메일"),
                fieldWithPath("profilePic").description("프로필 사진"));
    }

}
