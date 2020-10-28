function ONclick() {
    document.getElementById("checkbox").click()

    if (document.getElementById("checkbox").checked) {
        document.getElementById("input_low").disabled = true;
        document.getElementById('input_low').classList.add('disable');
    } else {
        document.getElementById("input_low").disabled = false;
        document.getElementById('input_low').classList.remove('disable');
    }
};

function only_num(input) {
    input.value = input.value.replace(/[^\d,]/g, '');
};

// ------------------------- Скрипт для загрузки фото -----------------------//

var a = document.getElementById("blah");
function readUrl(input, i) {
    if (input.files) {
        a = document.getElementById("blah" + i);
        var reader = new FileReader();
        reader.readAsDataURL(input.files[0]);
        reader.onload = (e) => {
            a.src = e.target.result;
        }
        var inputText = document.getElementById("text" + i);
        inputText.value="add";
        document.getElementById('delete_button' + i).classList.add('active_delete');
    }
};

var inputFile = document.getElementById("inputFile");
removeImg = (i) => {
    a = document.getElementById("blah" + i);
    inputFile = document.getElementById("inputFile" + i);   //IF ELSE
    a.src = "/static/img/load_image.jpg";
    inputFile.value = "";
    // readUrl(inputFile, i)
    var inputText = document.getElementById("text" + i);
    inputText.value="delete";
    document.getElementById('delete_button' + i).classList.remove('active_delete');
};