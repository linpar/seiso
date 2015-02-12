var app = angular.module('seisoControllers');

app.service('SearchService', function($http) {
  var baseUrl = 'internal/search?q=';
  var query = {};
  var results = {};

  // TODO replace concat with URI template?
  this.buildSearchUrl = function() { return baseUrl + query.value; }

  this.getQuery = function() { return query; };
  
  this.setQuery = function(newQuery) { query = { value : newQuery }; };
  
  this.getResults = function() { return results; };
  
  this.setResults = function(newResults) { results = { value : newResults }; };

  this.search = function(callback) {
    this.results = {};
    var searchRequest = {
      method: 'GET',
      url: this.buildSearchUrl(),
      headers: { 'Accept' : 'application/hal+json' }
    };
    $http(searchRequest).success(function(data) { 
      results = { value : data }; 
      callback();
     });
  };
});

app.controller('SearchController', [ '$rootScope', '$scope', 'SearchService', '$location', 
  function($rootScope, $scope, SearchService, $location) {
    $scope.searchService = SearchService;
    $scope.searchQuery = SearchService.getQuery();
    $scope.searchResults = SearchService.getResults();
    $scope.search = function() {
      SearchService.search(function() { 
        var searchResults = SearchService.getResults()['value'];
        $rootScope.searchResults = searchResults;
//        console.log(JSON.stringify(searchResults));
        $location.path('/search');
      });
    };
  }
]);
