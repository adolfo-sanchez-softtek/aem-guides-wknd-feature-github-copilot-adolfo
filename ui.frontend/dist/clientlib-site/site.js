/*
 * ATTENTION: The "eval" devtool has been used (maybe by default in mode: "development").
 * This devtool is neither made for production nor for readable output files.
 * It uses "eval()" calls to create a separate source file in the browser devtools.
 * If you are trying to read the output file, select a different devtool (https://webpack.js.org/configuration/devtool/)
 * or disable the default devtool with "devtool: false".
 * If you are looking for production-ready output files, see mode: "production" (https://webpack.js.org/configuration/mode/).
 */
/******/ (function() { // webpackBootstrap
/******/ 	var __webpack_modules__ = ({

/***/ "./src/main/webpack/site/main.scss":
/*!*****************************************!*\
  !*** ./src/main/webpack/site/main.scss ***!
  \*****************************************/
/***/ (function(__unused_webpack_module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n// extracted by mini-css-extract-plugin\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/main.scss?");

/***/ }),

/***/ "./src/main/webpack/site/main.ts":
/*!***************************************!*\
  !*** ./src/main/webpack/site/main.ts ***!
  \***************************************/
/***/ (function(__unused_webpack_module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony import */ var _main_scss__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./main.scss */ \"./src/main/webpack/site/main.scss\");\n/* harmony import */ var C_Users_adolfo_sanchez_git_aem_guides_wknd_ui_frontend_src_main_webpack_site_main_ts__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./src/main/webpack/site/main.ts */ \"./src/main/webpack/site/main.ts\");\n/* harmony import */ var C_Users_adolfo_sanchez_git_aem_guides_wknd_ui_frontend_src_main_webpack_components_helloworld_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./src/main/webpack/components/_helloworld.js */ \"./src/main/webpack/components/_helloworld.js\");\n/* harmony import */ var C_Users_adolfo_sanchez_git_aem_guides_wknd_ui_frontend_src_main_webpack_components_helloworld_js__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(C_Users_adolfo_sanchez_git_aem_guides_wknd_ui_frontend_src_main_webpack_components_helloworld_js__WEBPACK_IMPORTED_MODULE_2__);\n\r\n;\r\n\r\n\r\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/site/main.ts?");

/***/ }),

/***/ "./src/main/webpack/components/_helloworld.js":
/*!****************************************************!*\
  !*** ./src/main/webpack/components/_helloworld.js ***!
  \****************************************************/
/***/ (function() {

eval("// Example of how a component should be initialized via JavaScript\r\n// This script logs the value of the component's text property model message to the console\r\n\r\n(function() {\r\n    \"use strict\";\r\n\r\n    // Best practice:\r\n    // For a good separation of concerns, don't rely on the DOM structure or CSS selectors,\r\n    // but use dedicated data attributes to identify all elements that the script needs to\r\n    // interact with.\r\n    var selectors = {\r\n        self:      '[data-cmp-is=\"helloworld\"]',\r\n        property:  '[data-cmp-hook-helloworld=\"property\"]',\r\n        message:   '[data-cmp-hook-helloworld=\"model\"]'\r\n    };\r\n\r\n    function HelloWorld(config) {\r\n\r\n        function init(config) {\r\n            // Best practice:\r\n            // To prevents multiple initialization, remove the main data attribute that\r\n            // identified the component.\r\n            config.element.removeAttribute(\"data-cmp-is\");\r\n\r\n            var property = config.element.querySelectorAll(selectors.property);\r\n            property = property.length == 1 ? property[0].textContent : null;\r\n\r\n            var model = config.element.querySelectorAll(selectors.message);\r\n            model = model.length == 1 ? model[0].textContent : null;\r\n\r\n            if (console && console.log) {\r\n                console.log(\r\n                    \"HelloWorld component JavaScript example\",\r\n                    \"\\nText property:\\n\", property,\r\n                    \"\\nModel message:\\n\", model\r\n                );\r\n            }\r\n        }\r\n\r\n        if (config && config.element) {\r\n            init(config);\r\n        }\r\n    }\r\n\r\n    // Best practice:\r\n    // Use a method like this mutation obeserver to also properly initialize the component\r\n    // when an author drops it onto the page or modified it with the dialog.\r\n    function onDocumentReady() {\r\n        var elements = document.querySelectorAll(selectors.self);\r\n        for (var i = 0; i < elements.length; i++) {\r\n            new HelloWorld({ element: elements[i] });\r\n        }\r\n\r\n        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;\r\n        var body             = document.querySelector(\"body\");\r\n        var observer         = new MutationObserver(function(mutations) {\r\n            mutations.forEach(function(mutation) {\r\n                // needed for IE\r\n                var nodesArray = [].slice.call(mutation.addedNodes);\r\n                if (nodesArray.length > 0) {\r\n                    nodesArray.forEach(function(addedNode) {\r\n                        if (addedNode.querySelectorAll) {\r\n                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));\r\n                            elementsArray.forEach(function(element) {\r\n                                new HelloWorld({ element: element });\r\n                            });\r\n                        }\r\n                    });\r\n                }\r\n            });\r\n        });\r\n\r\n        observer.observe(body, {\r\n            subtree: true,\r\n            childList: true,\r\n            characterData: true\r\n        });\r\n    }\r\n\r\n    if (document.readyState !== \"loading\") {\r\n        onDocumentReady();\r\n    } else {\r\n        document.addEventListener(\"DOMContentLoaded\", onDocumentReady);\r\n    }\r\n\r\n}());\r\n\n\n//# sourceURL=webpack://aem-maven-archetype/./src/main/webpack/components/_helloworld.js?");

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/compat get default export */
/******/ 	!function() {
/******/ 		// getDefaultExport function for compatibility with non-harmony modules
/******/ 		__webpack_require__.n = function(module) {
/******/ 			var getter = module && module.__esModule ?
/******/ 				function() { return module['default']; } :
/******/ 				function() { return module; };
/******/ 			__webpack_require__.d(getter, { a: getter });
/******/ 			return getter;
/******/ 		};
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/define property getters */
/******/ 	!function() {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = function(exports, definition) {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	!function() {
/******/ 		__webpack_require__.o = function(obj, prop) { return Object.prototype.hasOwnProperty.call(obj, prop); }
/******/ 	}();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	!function() {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = function(exports) {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	}();
/******/ 	
/************************************************************************/
/******/ 	
/******/ 	// startup
/******/ 	// Load entry module and return exports
/******/ 	// This entry module is referenced by other modules so it can't be inlined
/******/ 	var __webpack_exports__ = __webpack_require__("./src/main/webpack/site/main.ts");
/******/ 	
/******/ })()
;