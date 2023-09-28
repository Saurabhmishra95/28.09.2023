package com.experianhealth.ciam.scimapi.entity;

public class GroupMember {
	

		private String value;
	    private String $ref;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String get$ref() {
			return $ref;
		}
		public void set$ref(String $ref) {
			this.$ref = $ref;
		}

	  
		   @Override
		    public String toString() {
		        return "GroupMember{" +
		                "value='" + value + '\'' +
		                ", ref='" + $ref + '\'' +
		                '}';
		    }
	}



