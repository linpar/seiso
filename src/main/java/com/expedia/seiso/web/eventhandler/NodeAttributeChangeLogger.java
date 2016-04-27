package com.expedia.seiso.web.eventhandler;

import java.io.Serializable;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;

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
    	if (entity instanceof Node){
    		for ( int i=0; i < propertyNames.length  && !updated; i++ ) {
    			if (currentState[i] != null){
	                if ( !currentState[i].equals(previousState[i]) && !propertyNames[i].equals("healthState")) {
	                	updated = true;
	                }
    			}
            }
        	if (updated){
        		log.info(getNodeInfo((Node)entity));
        	}
    	}
        return false;
    }
    
    private String getNodeInfo(Node node){
		StringBuilder content = new StringBuilder();
		content.append("Node attributes: \r\n");
		content.append("  Id:" + node.getId() + "\r\n");
		content.append("  Name: " + node.getName() + "\r\n");
		content.append("  Description: " + node.getDescription() + "\r\n");
		content.append("  Version:" + node.getVersion() + "\r\n");
		content.append("  Build Version:" + node.getBuildVersion() + "\r\n");
		
		content.append("  Health Status Link:" + node.getHealthStatusLink() + "\r\n");
		content.append("  Health Status Reason:" + node.getHealthStatusReason() + "\r\n");
		if (node.getHealthStatus() != null){
			content.append("  Health Status Id:" + node.getHealthStatus().getId() + "\r\n");
		} else {
			content.append("  Health Status Id: Null\r\n");
		}
		if (node.getMachine() != null) {
			content.append("  Machine Id:" + node.getMachine().getId() + "\r\n");
		} else {
			content.append("  Machine Id: Null\r\n");
		}
		if (node.getAggregateRotationStatus() != null) { 
			content.append("  Aggregation Rotation Status Id:" + node.getAggregateRotationStatus().getId() + "\r\n");
		} else {
			content.append("  Aggregation Rotation Status Id: Null\r\n");
		}
		if (node.getServiceInstance() != null) {
			content.append("  Service Instance Id:" + node.getServiceInstance().getId() + "\r\n");
		} else {
			content.append("  Service Instance Id: Null\r\n");
		}
		content.append("  Ip Addresses:\r\n");
		List<NodeIpAddress> addresses = node.getIpAddresses();
		for (NodeIpAddress address : addresses){
			content.append("    " + address.getIpAddress() + "\r\n");
		}
		return content.toString();
	}

}	
