from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from apiclient import discovery
import httplib2
from oauth2client import client
from oauth2client.file import Storage
import json
import os

home_dir = os.path.expanduser('~')

# change this to wherever you want to store 
credential_dir = os.path.join(home_dir, '.credentials')

# change this to your client secret path
CLIENT_SECRET_FILE = os.path.join(home_dir, 'path_to_client_secret.json')

# change to post request if required
@csrf_exempt
def google_calendar(request):
	if request.method == 'GET':
		print("MEthod is get")
		id_token = request.GET.get('id_token', '')
		email = request.GET.get('email', '')
		print(id_token)
		print(email)

	else:
		id_token = request.POST.get('id_token', '')
		email = request.POST.get('email', '')

	return HttpResponse(json.dumps(get_return_json(email, id_token, 
		['https://www.googleapis.com/auth/calendar', 'profile', 'email'],
		'https://yoururl.com/Authenticate/google_calendar/', 'calendar'), 
	sort_keys=True, indent=4), content_type='application/json')

@csrf_exempt
def gmail(request):
	if request.method == 'GET':
		id_token = request.GET.get('id_token', '')
		email = request.GET.get('email', '')

	else:
		id_token = request.POST.get('id_token', '')
		email = request.POST.get('email', '')

	return HttpResponse(json.dumps(get_return_json(email, id_token, 
		['https://mail.google.com', 'profile', 'email'],
		'https://yoururl.com/Authenticate/gmail/', 'gmail'), 
	sort_keys=True, indent=4), content_type='application/json')

@csrf_exempt
def drive(request):
	if request.method == 'GET':
		id_token = request.GET.get('id_token', '')
		email = request.GET.get('email', '')

	else:
		id_token = request.POST.get('id_token', '')
		email = request.POST.get('email', '')

	return HttpResponse(json.dumps(get_return_json(email, id_token, 
		['https://www.googleapis.com/auth/drive', 'profile', 'email'],
		'https://yoururl.com/Authenticate/drive/', 'drive'), 
	sort_keys=True, indent=4), content_type='application/json')

@csrf_exempt
def gmail_readonly(request):
	if request.method == 'GET':
		id_token = request.GET.get('id_token', '')
		email = request.GET.get('email', '')

	else:
		id_token = request.POST.get('id_token', '')
		email = request.POST.get('email', '')

	return HttpResponse(json.dumps(get_return_json(email, id_token, 
		['https://www.googleapis.com/auth/gmail.readonly', 'profile', 'email'],
		'https://yoururl.com/Authenticate/gmail_readonly/', 'gmail_readonly'), 
	sort_keys=True, indent=4), content_type='application/json')

@csrf_exempt
def drive_readonly(request):
	if request.method == 'GET':
		id_token = request.GET.get('id_token', '')
		email = request.GET.get('email', '')

	else:
		id_token = request.POST.get('id_token', '')
		email = request.POST.get('email', '')

	return HttpResponse(json.dumps(get_return_json(email, id_token, 
		['https://www.googleapis.com/auth/drive.readonly', 'profile', 'email'],
		'https://yoururl.com/Authenticate/drive_readonly/', 'drive_readonly'), 
	sort_keys=True, indent=4), content_type='application/json')

# Returns the json to be returned to the user
def get_return_json(email, id_token, scopes, redirect_uri, path):
	ret_json = {}
	if not id_token or not email or not redirect_uri or not len(scopes):
		ret_json['status'] = 'error'
		ret_json['message'] = 'Invalid parameters supplied'
		return ret_json

	try:
		credentials = get_credentials(email, scopes, path)
		if not credentials:
			credentials = client.credentials_from_clientsecrets_and_code(
				CLIENT_SECRET_FILE,
				scopes,
				id_token,
				redirect_uri=redirect_uri)
			http_auth = credentials.authorize(httplib2.Http())
			v = os.path.join(credential_dir, path)
			store = Storage(os.path.join(v, credentials.id_token['email']+'.json'))
			store.put(credentials)
			
		if not credentials or credentials.invalid or not credentials.has_scopes(scopes):
			print('Invalid credentials, raising exception')
			raise Exception("Invalid credentials")

		ret_json['status'] = 'success'
		ret_json['message'] = 'Successfully authenticated user.'
		
	except Exception as e:
		print('Exception is ')
		print(e)
		ret_json['status'] = 'error'
		ret_json['message'] = 'Error authenticating user.'

	return ret_json

# returns None if credentials do not have required scopes
def get_credentials(email, scopes, path):
	credential_path = os.path.join(credential_dir, path+'/'+email+'.json')
	store = Storage(credential_path)
	credentials = store.get()
	if not credentials or credentials.invalid or not credentials.has_scopes(scopes):
		return None
	return credentials
