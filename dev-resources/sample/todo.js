/* global jQuery */
"use strict";
(function($) {
 
  function pde(e) { if (e.preventDefault) e.preventDefault(); else e.returnValue = false; }
  
  function login(e) {
    pde(e);
    var username = $("#login-username").val(),
        password = $("#login-password").val(),
        $error = $("#login .error-msg");
    if (password !== username) {
      $error.show();
    } else {
      $error.hide();
      $(".username").text(username);
      $("#todo ul.todo").empty();
      $(".page").hide();
      $("#todo").show();
      $("#new-todo-text").focus();
    }
    return false;
  }
  
  function logout(e) {
    pde(e);
    $("#login-password").val("");
    $(".page").hide();
    $("#login").show();
    $("#login-username").focus();
    return false;
  }
  
  function addTodo(e) {
    pde(e);
    var $todo = $("#new-todo-text"),
        text = $todo.val(),
        $todos = $("#todo ul.todo");
    if (text && text.length) $todos.append($("<li>").text(text));
    $todo.val("").focus();
    return false;
  }
  
  function init() {
    $("#todo")
      .find("form button[type='submit']").click(addTodo).end()
      .find("a.logout").click(logout).end();
    $("#login")
      .find("input").val("").end()
      .find("button[type='submit']").click(login).end()
      .show();
  }
  
  $(init);
  
})(jQuery);
