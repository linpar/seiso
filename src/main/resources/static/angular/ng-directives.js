var enterDirective = function() {
	var directive = function() {
		return function(scope, element, attrs) {
			element.bind("keydown keypress", function(event) {
				if (event.which === 13) {
					scope.$apply(function() { scope.$eval(attrs.ngEnter); });
					event.preventDefault();
				}
			});
		}
	}
	return [ directive ];
}

// http://stackoverflow.com/questions/21362712/html-file-as-content-in-bootstrap-popover-in-angularjs-directive
var rotationDetailsPopoverDirective = function() {
	var directive = function($compile, $templateCache, $http) {
		return {
			restrict: "A",
			link: function($scope, $element, $attrs) {
				
				// Get the template here, not outside this function. Otherwise the behavior is all funky when the user
				// activates multiple popups, applies the filter, etc.
				// http://stackoverflow.com/questions/16122139/angular-js-jquery-html-string-parsing-in-1-9-1-vs-1-8-3
				var template = $templateCache.get("rotationDetailsPopover.html");
				template = angular.element($.trim(template));
				
				$http.get($attrs.rotationDetailsPopover).success(function(data) {
					$scope.ipAddress = data;
				});
				
				var popoverContent = $compile(template)($scope);
				$($element).popover({
					title : "Rotation Details",
					content : popoverContent,
					placement : "top",
					html : true,
					date : $scope.date
				});
			}
		}
	}
	return [ '$compile', '$templateCache', '$http', directive ];
}

// Register directives
angular.module('seiso')
		.directive('ngEnter', enterDirective())
		.directive('rotationDetailsPopover', rotationDetailsPopoverDirective())
		;
