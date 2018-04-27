# OAuth2 Django with Android

This project is for developers who wish to authenticate their users with a backend server. To read more, visit

https://developers.google.com/identity/sign-in/web/backend-auth

To get this project up and running, you must have a Web Application (create one if you don't have one) on your Google Developers
Console account, and you have access to the Client Secret file.

The folders in this project are:

* Oauth2 Django Android: The android project, mainly written in Kotlin
* Authenticate: The Django Project.

Keep in mind that the Django project was built using Django 2.0 and Python 3

To get this project working, make the following changes:
* In Authenticate/google_auth/views.py: replace CLIENT_SECRET_FILE to the location of your client
secret file, credential_dir to where you want to store your user's credentials, and replace all
occurances of https://yoururl.com/... to your redirect URI specified in your Google Developers Console
* In Authenticate/Authenticate/settings.py, add your ip address or your url to ALLOWED_HOSTS
* In the Android project, in the strings.xml file, change ```server_client_id``` to the client id 
that you obtained from your google developer console, and change ```server_name``` to the ip address 
of your server or your domain name
