package br.com.oratech.service.dto;

import java.io.Serializable;
import java.util.Date;

public class MovimentacaoDTO implements Serializable{

	private static final long serialVersionUID = 6214305129766657950L;

	private Long id;
	
	private String tipoMovimentacao;
	
	private String categoriaMovimentacao;
	
	private Double valor;
	
	private Date dataMovimentacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(String tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public String getCategoriaMovimentacao() {
		return categoriaMovimentacao;
	}

	public void setCategoriaMovimentacao(String categoriaMovimentacao) {
		this.categoriaMovimentacao = categoriaMovimentacao;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Date getDataMovimentacao() {
		return dataMovimentacao;
	}

	public void setDataMovimentacao(Date dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoriaMovimentacao == null) ? 0 : categoriaMovimentacao.hashCode());
		result = prime * result + ((dataMovimentacao == null) ? 0 : dataMovimentacao.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((tipoMovimentacao == null) ? 0 : tipoMovimentacao.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MovimentacaoDTO other = (MovimentacaoDTO) obj;
		if (categoriaMovimentacao == null) {
			if (other.categoriaMovimentacao != null)
				return false;
		} else if (!categoriaMovimentacao.equals(other.categoriaMovimentacao))
			return false;
		if (dataMovimentacao == null) {
			if (other.dataMovimentacao != null)
				return false;
		} else if (!dataMovimentacao.equals(other.dataMovimentacao))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tipoMovimentacao == null) {
			if (other.tipoMovimentacao != null)
				return false;
		} else if (!tipoMovimentacao.equals(other.tipoMovimentacao))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}
}
