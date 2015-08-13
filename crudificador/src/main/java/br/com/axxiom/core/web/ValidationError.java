package br.com.axxiom.core.web;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {
    private List<FieldError> fieldErrors = new ArrayList<FieldError>();

    public ValidationError() {   
    	
    }

    public void addFieldError(String path, String message) {

        FieldError error = new FieldError(path, message);

        fieldErrors.add(error);

    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.setField(field);
            this.setMessage(message);
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
