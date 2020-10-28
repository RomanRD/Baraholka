function ONclick() {
  document.getElementById("checkbox").click()

  if (document.getElementById("checkbox").checked) {
    document.getElementById("input_low_1").disabled = false;
    document.getElementById('input_low_1').classList.remove('disable');

    document.getElementById("input_low_2").disabled = false;
    document.getElementById('input_low_2').classList.remove('disable');

    document.getElementById("input_low_3").disabled = false;
    document.getElementById('input_low_3').classList.remove('disable');

    document.getElementById("input_low_4").disabled = false;
    document.getElementById('input_low_4').classList.remove('disable');


    // document.getElementById('button_accept').classList.remove('disable_button');
  } else {
    document.getElementById("input_low_1").disabled = true;
    document.getElementById('input_low_1').classList.add('disable');

    document.getElementById("input_low_2").disabled = true;
    document.getElementById('input_low_2').classList.add('disable');

    document.getElementById("input_low_3").disabled = true;
    document.getElementById('input_low_3').classList.add('disable');

    document.getElementById("input_low_4").disabled = true;
    document.getElementById('input_low_4').classList.add('disable');


    // document.getElementById('button_accept').classList.add('disable_button');
  }
}


// $(document).ready(function () {
//
//   $("#sidebar_main").on("click", "#sidebar", function () {
//
//     $("#sidebar_main").find(".active_sidebar").removeClass("active_sidebar");
//
//     $(this).addClass("active_sidebar");
//
//     $("#sidebar").eq($(this).index()).addClass("active_sidebar");
//   });
//
// });



function only_num(input) {
  input.value = input.value.replace(/[^\d,]/g, '');
};


// ===================================================

var a = document.getElementById("blah");

function readUrl(input, i) {
  if (input.files) {
    a = document.getElementById("blah" + i);
    var reader = new FileReader();
    reader.readAsDataURL(input.files[0]);
    reader.onload = (e) => {
      a.src = e.target.result;
    }
    document.getElementById('delete_button1').classList.add('active_delete');
    // document.getElementById('delete_button2').classList.add('active_delete');

  }
}
