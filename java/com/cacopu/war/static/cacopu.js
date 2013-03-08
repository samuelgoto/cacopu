var picker = null;

// Create and render a Picker object for searching images.
function createPicker() {
	picker = new google.picker.PickerBuilder().
	addView(google.picker.ViewId.IMAGE_SEARCH).
	addView(google.picker.ViewId.PHOTO_UPLOAD).
	addView(google.picker.ViewId.PHOTOS).
	setCallback(pickerCallback).
	setRelayUrl(window.location.origin + '/static/rpc_relay.html').
	build();
	picker.setVisible(true);
}

// A simple callback implementation.
function pickerCallback(data) {
	picker.setVisible(false);
	window.console.log(data);
	var message = 'You picked: ' +
	((data.action == google.picker.Action.PICKED) ? data.docs[0].url : 'nothing');
	alert(message);
	// document.getElementById('result').appendChild(document.createTextNode(message));
}
