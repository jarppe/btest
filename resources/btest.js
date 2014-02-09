"use strict";
define(["jquery", "lodash"], function($, _) {
  
  function exists(e)   { return e && e.length; }
  function visible(e)  { return exists(e) && e.is(":visible"); }
  function disabled(e) { return exists(e) && e.prop("disabled"); }
  function enabled(e)  { return !disabled(e); }
  
  function doWaitUntil(selector, checks, d, attempts) {
    var element = selector && $(selector);
    if (_.every(checks, function(check) { return check(element); })) {
      d.resolve(element);
    } else {
      if (attempts >= 500) {
        d.reject();
      } else {
        setTimeout(function() { doWaitUntil(selector, d, checks, attempts + 1); }, 10);
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
    hash: function(d, h) {
      waitUntil(null, function() { return hash() === h; })
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    value: function(d, selector, v) {
      waitUntil(selector, visible, function($e) { return $e.val() === v; })
        .done(function() { d.resolve(); })
        .fail(d.reject);
    },
    setHash: function(d, h) {
      hash(h);
      d.resolve();
    },
    getHash:  function(d) {
      d.resolve(hash());
    },
    getValue: function(d, selector) {
      waitUntil(selector, visible)
        .done(function(element) { d.resolve({value: element.val()}); })
        .fail(d.reject);
    },
    setValue: function(d, selector, value) {
      waitUntil(selector, visible)
        .done(function(element) { element.val(value).change(); d.resolve(); })
        .fail(d.reject);
    },
    ping: function(d) {
      d.resolve({ping: Array.prototype.slice.call(arguments, 1)});
    }
  };

  var getNextCommand = {
    url:          "/dev/brotest/command",
    type:         "POST",
    data:         null,
    contentType:  "application/json; charset=utf-8",
    dataType:     "json"
  };
  
  function run() {
    $.ajax(getNextCommand).then(handleResponse, handleFailure);
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
    getNextCommand.data = JSON.stringify({status: "ok", result: result});
  }

  function failed(result) {
    getNextCommand.data = JSON.stringify({status: "fail", result: result});
  }

  function handleFailure() {
    console.log("getCommand: fail:", arguments);
    setTimeout(run, 10000);
  }

  run();
  
});
