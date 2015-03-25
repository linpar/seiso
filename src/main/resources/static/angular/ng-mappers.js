// Maps API responses to UI-friendly structures.

// Supports the data center list page.
var organizeDataCenters = function(srcProviders, generalRegions) {
	var destProviders = {};
	for (i = 0; i < srcProviders.length; i++) {
	
		// Initialize provider data structure.
		var srcProvider = srcProviders[i];
		var providerKey = srcProvider.key;
		destProviders[providerKey] = {
			'name' : srcProvider.name,
			'specialRegions' : {}
		};
		var destProvider = destProviders[providerKey];
		for (j = 0; j < generalRegions.length; j++) {
			var generalRegion = generalRegions[j];
			destProvider.specialRegions[generalRegion.key] = [];
		};
		
		// Distribute the provider's special regions into into general regional buckets.
		destProvider = destProviders[providerKey];
		var srcSpecialRegions = srcProvider.regions;
		for (j = 0; j < srcSpecialRegions.length; j++) {
			var srcSpecialRegion = srcSpecialRegions[j];
			var generalRegionKey = srcSpecialRegion.regionKey;
			destProvider.specialRegions[generalRegionKey].push(srcSpecialRegion);
		}
	}
	return destProviders;
}

// Adds percentages to the node stats object.
var enrichNodeStats = function(nodeStats) {
	nodeStats.percentHealthy = 100 * (nodeStats.numHealthy / nodeStats.numNodes);
	nodeStats.percentEnabled = 100 * (nodeStats.numEnabled / nodeStats.numNodes);
	nodeStats.percentHealthyGivenEnabled = 100 * (nodeStats.numHealthyGivenEnabled / nodeStats.numEnabled);
}

// Supports the service instance details page.
var nodePageToNodeRows = function(nodePage) {
	var nodes = nodePage._embedded.items;
	
	// Build the node table, which is really a list of IP addresses grouped by node. [WLW]
	var nodeRows = [];
	for (i = 0; i < nodes.length; i++) {
		var node = nodes[i];
		
		if (node._embedded.healthStatus == null) {
			node._embedded.healthStatus = {
				"key" : "unknown",
				"name" : "Unknown",
				"_embedded" : {
					"statusType" : { "key" : "warning" }
				}
			}
		}
		
		var ipAddresses = node._embedded.ipAddresses;
		var nodeEnabled = true;
		
		if (ipAddresses.length == 0) {
			// Handle special case where there aren't any IP addresses.
			var nodeRow = {
				"name" : node.name,
				"displayName" : node.name,
				"version" : node.version,
				"healthStatus" : node._embedded.healthStatus,
				"showActions" : true
			}
			nodeRows.push(nodeRow);
			nodeEnabled = false;
		} else {
			// Handle case where there are IP addresses.
			for (j = 0; j < ipAddresses.length; j++) {
				var ipAddress = ipAddresses[j];
				var nodeRow = {
					"name" : node.name,
					"ipAddress" : ipAddress.ipAddress,
					"ipAddressRole" : ipAddress._embedded.ipAddressRole.name,
					"endpoints" : ipAddress._embedded.endpoints,
					"aggregateRotationStatus" : ipAddress._embedded.aggregateRotationStatus
				};
				if (j == 0) {
					// Distinguish name from display name. We want to filter by name, but display by
					// displayName.
					nodeRow.displayName = node.name;
					nodeRow.version = node.version,
					nodeRow.healthStatus = node._embedded.healthStatus;
					nodeRow.showActions = true;
				}
				nodeRows.push(nodeRow);
				
				if (ipAddress._embedded.aggregateRotationStatus.key != "enabled") {
					nodeEnabled = false;
				}
			}
		}
	}
		
	return nodeRows;
}
