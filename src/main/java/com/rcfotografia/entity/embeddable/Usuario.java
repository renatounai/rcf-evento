package com.rcfotografia.entity.embeddable;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder @AllArgsConstructor @NoArgsConstructor
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(unique = true)
	private String email;
	private String senha;
	
}
