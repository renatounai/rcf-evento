package com.rcfotografia.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"modelo_id", "rede_social_id"}))
public class ModeloRedeSocial extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@EqualsAndHashCode.Include
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Modelo modelo;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private RedeSocial redeSocial;
	
	private String perfil;
}
