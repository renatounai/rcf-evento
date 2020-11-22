package com.rcfotografia.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.rcfotografia.entity.embeddable.Periodo;

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
public class Evento extends BaseEntity {	
	private static final long serialVersionUID = 1L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue
	private Integer id;
	
	@JoinColumn
	@ManyToOne(fetch = FetchType.LAZY)
	private EventoTipo tipo;
	
	@Column(precision = 9, scale = 2)
	private BigDecimal valorCobrado;
	
	@Embedded
	private Periodo dataPrevista;
	
}
