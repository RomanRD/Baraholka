$(document).ready(function () {

  $(".registration_form").on("click", ".tab", function () {

    $(".registration_form").find(".active").removeClass("active");

    $(this).addClass("active");

    $(".tab-form").eq($(this).index()).addClass("active");
  });

});
