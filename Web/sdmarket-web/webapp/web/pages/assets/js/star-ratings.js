let star = document.querySelectorAll('[type="radio"]');

for (let i = 0; i < star.length; i++) {
	star[i].addEventListener('click', function() {
		i = this.value;
	});
}