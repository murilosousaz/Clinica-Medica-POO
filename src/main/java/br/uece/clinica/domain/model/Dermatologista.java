package br.uece.clinica.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DERMATOLOGISTA")
@NoArgsConstructor
@AllArgsConstructor
public class Dermatologista extends Medico {

}