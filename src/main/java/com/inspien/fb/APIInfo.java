package com.inspien.fb;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder; //lombok이란, 자바 라이브러리로 반복되는 getter, setter, toString등의 메서드 작성코드를 줄여주는 코드 다이어트 라이브러리이다.
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class APIInfo {
	private String app; //자동적으로 Getter, Setter, Builer가 생성된다.
	private String ver;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime timestamp;
}
