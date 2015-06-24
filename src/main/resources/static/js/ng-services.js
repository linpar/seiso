// TODO Take a look at $resource
// https://docs.angularjs.org/api/ngResource/service/$resource

// TODO Separate service definitions from registrations, like we do in the other modules.

angular.module('seisoServices', [])

	// FIXME Don't block on the response
	// Constructor function to create a service factory, which creates dependency-injected services.
	.factory('v1Api', ['$http', function($http) {
		// The object we're returning is the service.
		return {
			get: function(path) {
				// FIXME Return the promise, not the data...?
				return $http.get(path).success(function(data) { return data; });
			},
			
			// pageNumber is 0-indexed
			getPage: function(path, pageNumber, pageSize, sortKey) {
				console.log("Getting page: " + path);
				return $http.get(path + '?page=' + pageNumber + '&size=' + pageSize + '&sort=' + sortKey);
			}
		};
	}])
	
	.factory('v2Api', ['$http', function($http) {
		return {
			get: function(path, successHandler, errorHandler) {
				var request = {
					method: 'GET',
					url: path,
					headers: { 'Accept': 'application/hal+json' }
				}
				$http(request)
					.success(successHandler)
					.error(errorHandler);
			}
		}
	}])
	
	// TODO Is it better to use $location instead of $window?
	// $location.path() was making XHR calls over HTTP even though the page is HTTPS.
	.factory('AuthService', [ '$rootScope', '$http', '$window', function($rootScope, $http, $window) {
		var checkAuthentication = function(login) {
			console.log("Checking authentication");
			var successHandler = function(data) {
				$rootScope.authenticated = (data.name ? true : false);
				console.log("Authenticated: " + $rootScope.authenticated);
				
				var isAnonymous = true;
				var hasAdminRole = false;
				
				if ($rootScope.authenticated) {
					isAnonymous = false;					
					$rootScope.authorities = data.authorities;
					for (i = 0; i < data.authorities.length; i++) {
						var authority = data.authorities[i];
						if (authority.authority == "ROLE_ADMIN") {
							hasAdminRole = true;
						}
					}
				}
				
				$rootScope.isAnonymous = isAnonymous;
				$rootScope.hasAdminRole = hasAdminRole;
				
				if (login) {
					if ($rootScope.authenticated) {
						$rootScope.authenticationError = false;
						// When this was $location.path("/"), it was doing XHR over HTTP rather than HTTPS.
						$window.location.href = "/";
					} else {
						$rootScope.authenticationError = true;
					}
				}
			}
			var errorHandler = function() {
				console.log("Authentication check failed");
				$rootScope.authenticated = false;
			}
			$http.get("/internal/security/user")
					.success(successHandler)
					.error(errorHandler);
		}
		return {
			checkAuthentication: checkAuthentication,
			login: function(credentials) {
				console.log("Authenticating");
				var request = {
					method: 'POST',
					url: '/login',
					headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
					data: "username=" + encodeURIComponent(credentials.username) + "&password=" + encodeURIComponent(credentials.password)
				}
				var successHandler = function() {
					// This just means that the request succeeded, not that authentication succeeded.
					// Whether authentication succeeded or not, /login returns an HTTP 302 redirect.
					// So we still need to check.
					checkAuthentication(true);
				}
				var errorHandler = function() {
					console.log("Authentication failed");
					$rootScope.authenticated = false;
				}
				$http(request)
						.success(successHandler)
						.error(errorHandler);
			},
			logout: function() {
				console.log("Logging out");
				var successHandler = function() {
					console.log("Logged out");
					$rootScope.authenticated = false;
					$window.location.href = "/";
				}
				var errorHandler = function(data) {
					// TODO Is this the right thing to do here?
					// Can't really assume that the user is logged out.
					$rootScope.authenticated = false;
				}
				$http.post('logout', {})
						.success(successHandler)
						.error(errorHandler);
			}
		}
	}])
	
	// This is a service, not a factory.
	.service('SearchService', [ '$http', function($http) {
		var baseUrl = 'internal/search?q=';
		var query = {};
		var results = {};
	
		// TODO replace concat with URI template?
		this.buildSearchUrl = function() { return baseUrl + query.value; }
		this.getQuery = function() { return query; }
		this.setQuery = function(newQuery) { query = { value : newQuery } }
		this.getResults = function() { return results; }
		this.setResults = function(newResults) { results = { value : newResults } }
		
		this.search = function(callback) {
			this.results = {};
			var searchRequest = {
				method : 'GET',
				url : this.buildSearchUrl(),
				headers : { 'Accept' : 'application/hal+json' }
			}
			$http(searchRequest)
					.success(function(data) {
						results = { value : data }
						callback();
					});
		};
	}])
	
	;
