package br.com.axxiom.core.web;

import java.util.List;

import org.springframework.data.domain.Page;

import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

public final class DatatablesResponse<T> {

    private List<T> aaData = null;
    private Long iTotalRecords = 1l;
    private Long iTotalDisplayRecords = 1l;
    private Integer sEcho = 1;

      private DatatablesResponse(Page<T> page, DatatablesCriterias criterias) { 
    	  if (page!=null) {
    		  this.aaData = page.getContent(); 
    		  this.iTotalRecords = page.getTotalElements();
    		  this.iTotalDisplayRecords = page.getTotalElements();
    	  }
    	  else {
    		  this.aaData = null; 
    		  this.iTotalRecords = 0l;
    		  this.iTotalDisplayRecords = 0l;
    	  }
    	  this.sEcho = criterias.getInternalCounter(); 
      }
      
      public List<T> getAaData() { return aaData; }
      
      public Long getiTotalRecords() { return iTotalRecords; }
      
      public Long getiTotalDisplayRecords() { return iTotalDisplayRecords; }
      
      public Integer getsEcho() { return sEcho; }
      
      public static <T> DatatablesResponse<T> build(Page<T> page, DatatablesCriterias criterias) { 
    	  return new DatatablesResponse<T>(page, criterias); 
      }
     
}
