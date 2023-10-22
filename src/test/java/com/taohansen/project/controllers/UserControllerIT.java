package com.taohansen.project.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.taohansen.project.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;
	
	private String visitorUsername;
	private String visitorPassword;
	private String memberUsername;
	private String memberPassword;
	private String nonExistingUsername;
	private String nonExistingPass;
	
	@BeforeEach
	void setUp() throws Exception {
		
		visitorUsername = "visitor@gmail.com";
		visitorPassword = "123456";
		memberUsername = "member@gmail.com";
		memberPassword = "123456";
		nonExistingUsername = "ab@gmail.com";
		nonExistingPass = "123456";
	}

	@Test
	public void getProfileShouldReturnUnauthorizedWhenNotValidToken() throws Exception {

		ResultActions result =
				mockMvc.perform(get("/users/profile")
					.contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());
	}

	@Test
	public void getProfileShouldReturnProfileDataWhenMemberAuthenticated() throws Exception {

		String accessToken = tokenUtil.obtainAccessToken(mockMvc, memberUsername, memberPassword);

		ResultActions result =
				mockMvc.perform(get("/users/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(2L));
		result.andExpect(jsonPath("$.name").value("member_user"));
		result.andExpect(jsonPath("$.email").value("member@gmail.com"));
	}
}
