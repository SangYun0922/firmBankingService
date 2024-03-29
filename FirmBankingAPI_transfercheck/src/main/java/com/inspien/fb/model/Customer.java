package com.inspien.fb.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Customer {
	private String id;
	private String name;
	private String callback;
}
