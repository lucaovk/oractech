package br.com.axxiom.core.web;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import br.com.axxiom.core.service.dto.MultiOrderedSearch;
import br.com.axxiom.core.service.dto.OrderedSearch;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.ColumnDef.SortDirection;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

public class DatatablePageInfo<T> extends PageRequest {
    /**
     * 
     */
    private static final long serialVersionUID = 2026586539745978047L;
    private Sort sort = null;
   
    public DatatablePageInfo(DatatablesCriterias criterias, T search) { 
      super(criterias.getDisplayStart() / criterias.getDisplaySize(), criterias.getDisplaySize());
      List<ColumnDef> cols = criterias.getSortingColumnDefs(); 
      if (!cols.isEmpty()) { 
    	  ColumnDef col = cols.get(0); 
          if (search instanceof OrderedSearch) {
            sort = new Sort(((OrderedSearch) search).getDirection(), ((OrderedSearch) search).getSortedProperty()); 
          } else if (search instanceof MultiOrderedSearch) {
        	  sort = new Sort(((MultiOrderedSearch) search).getDirections());
          } else {
	    	  if (col.getSortDirection() == SortDirection.ASC) { 
	    		  sort = new Sort(Sort.Direction.ASC, col.getName()); 
	    	  } else { 
	    		  sort = new Sort(Sort.Direction.DESC, col.getName()); 
	    	  } 
          }
    	  
      } 
    }
      
      @Override public Sort getSort() { return sort; }
     
}
