const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container1');
const submitButton = document.getElementById('submit');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");

});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

function loginForm(event) {
	event.preventDefault(); // Prevent form submission

	const username = document.getElementById('login_username').value;
	const password = document.getElementById('login_password').value;

	validateLogin(username, password)
		.then(() => {
		// Login successful, redirect to the home page
		window.location.href = 'home.html';
		})
		.catch(error => {
		// Login failed, display error message
		console.error(error);
		alert('Invalid username or password');
	});
}

function validateLogin(username, password) {
	const url = 'http://localhost:8080/user/login';

	return fetch(url, {
		method: 'POST',
		mode: 'cors',
		headers: {
		'Content-Type': 'application/json'
		},
		body: JSON.stringify({ username, password })
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Login failed');
		}
		console.log(username)
		storeUsername('username', username);
	});
}


function signupForm(event) {
	event.preventDefault(); // Prevent form submission

	const username = document.getElementById('signup_username').value;
	const password = document.getElementById('signup_password').value;
	const re_password = document.getElementById('re_signup_password').value;

	if(password.localeCompare(re_password) == -1) {
		alert('Both passwords must match');
	}
	else {
		doSignup(username, password)
		.then(() => {
		// Signup successful, redirect to the home page
		window.location.href = 'home.html';
		})
		.catch(error => {
		// Signup failed, display error message
		console.error(error);
		alert('Signup failed');
	});
	}
}

function doSignup(username, password) {
	const url = 'http://localhost:8080/user/signup';

	return fetch(url, {
		method: 'POST',
		mode: 'cors',
		headers: {
		'Content-Type': 'application/json'
		},
		body: JSON.stringify({ username, password })
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Signup failed');
		}
		storeUsername('username', username);
	});
}

function storeUsername(name, value) {
	localStorage.setItem(name, value)
  }