package com.example.common.interfaces.rest.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {
	private int id;
	private String name;
	private String department;
	private long salary;
}
