package com.rcfotografia.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.rcfotografia.entity.embeddable.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Modelo extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private Integer id;
	
	@NotBlank
	private String nome;
	
	private String telefone;
	
	@Embedded
	private Usuario usuario;
	
	
}
