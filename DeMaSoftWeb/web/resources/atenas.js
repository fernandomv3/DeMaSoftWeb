
$("#buscarpedido").click(function(){
    //alert("xirving");
    var atenas = $("#busqueda").val();
    $("#inputatenas").val(atenas);
    $("#formAtenas").show("slow");
    
    return false;
});

$("#buscarcliente").click(function(){
    var atenas = $("#busqueda").val();
    $("#inputatenas").val(atenas);
    $("#formAtenas").show("slow");
    
    return false;
});

$("#buscarchofer").click(function(){
    var atenas = $("#busqueda").val();
    $("#inputatenas").val(atenas);
    $("#formAtenas").show("slow");
    
    return false;
});

$('#inputDate').DatePicker({
	format:'m/d/Y',
	date: $('#inputDate').val(),
	current: $('#inputDate').val(),
	starts: 1,
	position: 'r',
	onBeforeShow: function(){
		$('#inputDate').DatePickerSetDate($('#inputDate').val(), true);
	},
	onChange: function(formated, dates){
		$('#inputDate').val(formated);
		if ($('#closeOnSelect input').attr('checked')) {
			$('#inputDate').DatePickerHide();
		}
	}
});