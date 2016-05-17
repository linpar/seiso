package com.expedia.seiso.hbn;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.expedia.seiso.domain.entity.Node;

import lombok.extern.slf4j.XSlf4j;

@XSlf4j
public class NodeAttributeChangeLogger extends EmptyInterceptor {
    
	private static final long serialVersionUID = 10009L;

    @Override
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {
    	boolean updated = false;
    	StringBuilder content = new StringBuilder();
    	if (entity instanceof Node){
    		for ( int i=0; i < propertyNames.length  && !updated; i++ ) {
    			if (currentState[i] != null){
	                if ( !currentState[i].equals(previousState[i])) {
	                	if (!updated){
	                		content.append("Node changed:\r\n");
	                		updated = true;
	                	} 
	                	content.append("   Attribute: " + propertyNames[i] + "," + " previous value: " + 
	                			previousState[i].toString() + ", current value: " + currentState[i]);
	                }
    			}
            }
        	if (updated){
        		log.info(content.toString());
        	}
    	}
        return false;
    }
}	
