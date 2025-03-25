package com.iguroo.task.service;

import java.util.Map;

import com.iguroo.task.dto.LoginDto;
import com.iguroo.task.dto.UserDto;

public interface AuthService {

	String register(UserDto userDto);

	 String login(LoginDto loginDto);

	String registerAdmin(UserDto userDto, Long adminId);

}
