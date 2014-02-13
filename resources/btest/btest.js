"use strict";
(function (factory) {
  if (typeof require === "function" && typeof exports === "object" && typeof module === "object") {
    factory(require("jquery"));
  } else if (typeof define === "function" && define.amd) {
    define(["jquery"], factory);
  } else {
    /* global jQuery */
    factory(jQuery);
  }
}(function($) {
  
  console.log("btest: initializing...");
  
  var $idle     = $("#head .idle"),
      $testing  = $("#head .testing"),
      $appDiv   = $("#app"),
      appUrl    = null;

  function loadApp(d, url) {
    appUrl = url;
    $appDiv.empty().append("<iframe id='appframe'>");
    $("#appframe").attr("src", url).load(function() { d.resolve(); });
  }
  
  function reload(d) { loadApp(d, appUrl); }
  
  function idle() { $testing.hide(); $idle.show(); }
  function testing() { $idle.hide(); $testing.show(); }
  
  function every(coll, p) {
    var i, len = coll.length;
    for (i = 0; i < len; i++) {
      if (!p(coll[i])) return false;
    }
    return true;
  }
  
  function exists(e)    { return e && e.length; }
  function visible(e)   { return exists(e) && e.is(":visible"); }
  function invisible(e) { return !visible(e); }
  function disabled(e)  { return exists(e) && e.prop("disabled"); }
  function enabled(e)   { return !disabled(e); }
  
  function doWaitUntil(selector, checks, d, attempts) {
    var element = selector && $("#appframe").contents().find(selector);
    if (every(checks, function(check) { return check(element); })) {
      d.resolve(element);
    } else {
      if (attempts >= 100) {
        d.reject();
      } else {
        setTimeout(function() { doWaitUntil(selector, checks, d, attempts + 1); }, 10);
      }
    }
  }
  
  function waitUntil(selector) {
    var checks = Array.prototype.slice.call(arguments, 1),
        d = new $.Deferred();
    doWaitUntil(selector, checks, d, 0);
    return d.promise();
  }
  
  function hash(v) {
    return v ? window.location.hash = v : window.location.href.split("#")[1];
  }
  
  var commands = {
    "load-app": loadApp,
    reload: reload,
    exists: function(d, selector) {
      waitUntil(selector, exists)
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    visible: function(d, selector) {
      waitUntil(selector, visible)
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    invisible: function(d, selector) {
      waitUntil(selector, invisible)
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    enabled: function(d, selector) {
      waitUntil(selector, enabled)
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    disabled: function(d, selector) {
      waitUntil(selector, disabled)
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    click: function(d, selector) {
      waitUntil(selector, visible, enabled)
        .done(function(element) { element[0].click(); d.resolve(); })
        .fail(d.reject);
    },
    "url-hash": function(d, h) {
      waitUntil(null, function() { return hash() === h; })
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    value: function(d, selector, v) {
      waitUntil(selector, visible, function($e) { return $e.val() === v; })
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    text: function(d, selector, v) {
      waitUntil(selector, visible, function($e) { console.log("text:", $e, $e.text()); return $e.text() === v; })
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    "set-hash": function(d, h) {
      hash(h);
      d.resolve();
    },
    "get-hash":  function(d) {
      d.resolve(hash());
    },
    "get-value": function(d, selector) {
      waitUntil(selector, visible)
        .done(function(element) { d.resolve({value: element.val()}); })
        .fail(d.reject);
    },
    "set-value": function(d, selector, value) {
      waitUntil(selector, visible)
        .done(function(element) { element.val(value).change(); d.resolve(); })
        .fail(d.reject);
    },
    ping: function(d) {
      d.resolve({ping: Array.prototype.slice.call(arguments, 1)});
    }
  };

  var getNextCommand = {
    url:          "/btest",
    type:         "POST",
    contentType:  "application/json; charset=utf-8",
    dataType:     "json"
  };
  
  function run() {
    idle();
    getNextCommand.data = JSON.stringify(getNextCommand.data || {});
    $.ajax(getNextCommand)
      .always(testing)
      .then(handleResponse, handleFailure);
    getNextCommand.data = null;
    return null;
  }
  
  function handleResponse(response) {
    if (response.status === "command") {
      return executeCommand(response.command, response.args).then(success, failed).always(run);
    } else if (response.status === "timeout") {
      return run();
    }
    console.log("what?", response);
    setTimeout(run, 10000);
  }
  
  function executeCommand(commandName, args) {
    var d = new $.Deferred(),
        command = commands[commandName];
    if (command) {
      args.unshift(d);
      command.apply(d, args);
    } else {
      d.reject("unknown command");
    }
    return d.promise();
  }
  
  function success(result) {
    getNextCommand.data = {status: "ok", result: result};
  }

  function failed(result) {
    getNextCommand.data = {status: "fail", result: result};
  }

  function handleFailure() {
    setTimeout(run, 10000);
  }

  run();
  
}));
