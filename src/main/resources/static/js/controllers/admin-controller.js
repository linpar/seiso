// FIXME Break this up into multiple controllers so we don't have to load so much data at once. [WLW]
var adminController = function() {
	var controller = function($scope, $http) {
		$scope.model.page.title = pageTitle('Admin Console');
		console.log('Getting health');
		$http.get('health').success(function(data) {
			console.log('Got health');
			$scope.health = data;
			$scope.seisoLabel = (data.status === 'UP' ? 'success' : 'danger');
			$scope.diskLabel = (data.diskSpace.status === 'UP' ? 'success' : 'danger');
			$scope.dbLabel = (data.db.status === 'UP' ? 'success' : 'danger');
			$scope.rabbitLabel = (data.rabbit.status === 'UP' ? 'success' : 'danger');
		})
		
		console.log('Getting metrics');
		$http.get('metrics').success(function(data) {
			console.log('Got metrics');
			$scope.metrics = data;
		})
		
		console.log('Getting environment');
		$http.get('env').success(function(data) {
			console.log('Got environment');
			$scope.env = data;
			$scope.appConfig = data['applicationConfig: [classpath:/application.yml]'];
			$scope.systemConfig = data['systemProperties'];
		})
		
		console.log('Getting thread dump');
		$http.get('dump').success(function(data) {
			console.log('Got thread dump');
			$scope.threads = data;
		});
	}
	return [ '$scope', '$http', controller ];
}
