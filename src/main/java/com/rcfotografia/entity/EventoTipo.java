package com.rcfotografia.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

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
public class EventoTipo extends BaseEntity {
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private Integer id;
	
	
	@NotEmpty
	@Column(unique = true)
	private String nome;
}
